package com.roncoo.eshop.cache.controller;

import com.roncoo.eshop.cache.hystrix.ProductInfoCommand;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.pojo.ApiResponse;
import com.roncoo.eshop.cache.prewarm.CachePrewarmThread;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.ProductInfoService;
import com.roncoo.eshop.cache.service.RebuildCacheService;
import com.roncoo.eshop.cache.service.ShopInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@EnableScheduling
@RequestMapping("/ehcache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private RebuildCacheService rebuildCacheService;

    /**
     * 获取商品信息
     * 1、从redis里面查 ==> 有，直接返回
     * 2、redis没有，从本地ehcache获取  ==> 有，直接返回
     * 3、本地ehcache也没有，从mysql中查询
     * 4、查询到了，将结果更新到ehcache和redis。重建缓存
     *
     * @param productId
     * @return
     */
    @GetMapping("/getProductInfo")
    public ProductInfo getProductInfo(Long productId) {
        log.info("[START] get product info by productId = {}", productId);
        ProductInfo productInfo = null;

        // 从redis中获取
        productInfo = cacheService.getProductInfoFromRedisCache(productId);

        if (productInfo != null) {
            log.info("get product info from redis success. productId = {}", productId);
            return productInfo;
        } else {
            log.info("get product info from redis failed. productId = {}", productId);
            // 从ehcache中获取
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            if (productInfo != null) {
                log.info("get product info from ehcache success. productId = {}", productId);
                return productInfo;
            }
        }
        // todo: redis和ehcache都没有。需要重新从mysql获取数据，重建缓存  --- 存在并发冲突的问题
        // 从mysql查询到数据后，存入本地缓存一份，直接返回nginx。同时将数据放入内存队列，单独起个线程去处理缓存的被动重建
        productInfo = productInfoService.getProductInfoById(productId);
        cacheService.saveProductInfo2LocalCache(productInfo);
        log.info("send productInfo to rebuildCacheQueue, waiting for rebuild cache", productInfo);
        rebuildCacheService.addMsg(productInfo);
        log.info("get productInfo from mysql. productId = {}, productInfo = {}", productId, productInfo);
        return productInfo;
    }

    @GetMapping("/getProductInfosByHystrix")
    public List<ProductInfo> getProductInfosByHystrix(String productIds) {
        List<ProductInfo> productInfos = new ArrayList<>();
        log.info("getProductInfo by hystrix productIds = {}", productIds);
        for (String id : productIds.split(",")) {
            ProductInfo productInfo = productInfoService.getProductByHystrix(id);
            if (productInfo != null) productInfos.add(productInfo);
        }
        return productInfos;
    }

    @GetMapping("/getProductInfosBySentinel")
    public List<ProductInfo> getProductInfosBySentinel(String productIds) {
        List<ProductInfo> productInfos = new ArrayList<>();
        log.info("getProductInfo by hystrix productIds = {}", productIds);
        for (String id : productIds.split(",")) {
            ProductInfo productInfo = productInfoService.getProductInfosBySentinel(id);
            if (productInfo != null) productInfos.add(productInfo);
        }
        return productInfos;
    }

    @GetMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        log.info("[START] get shop info by shopId = {}", shopId);
        ShopInfo shopInfo = null;
        // 从redis中获取
        shopInfo = cacheService.getShopInfoFromRedisCache(shopId);

        if (shopInfo != null) {
            log.info("get shop info from redis success. shopId = {}", shopId);
            return shopInfo;
        } else {
            log.info("get shop info info from redis failed. shopId = {}", shopId);
            // 从ehcache中获取
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
            if (shopInfo != null) {
                log.info("get shop info from ehcache success. productId = {}", shopId);
                return shopInfo;
            }
        }
        // todo: redis和ehcache都没有。需要重新从mysql获取数据，重建缓存
        log.info("get shop info info from ehcache failed. shopId = {}", shopId);
        shopInfo = shopInfoService.getShopInfoById(shopId);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        log.info("send shopInfo to rebuildCacheQueue, waiting for rebuild cache", shopInfo);

        // 缓存被动重建
        rebuildCacheService.addMsg(shopInfo);

        log.info("get product info. shopId = {}, shopInfo = {}", shopId, shopInfo);
        return shopInfo;
    }

    /**
     * 缓存预热的触发接口
     */
    @GetMapping("/warmUpCache")
    @Scheduled(cron = "0 */1 * * * ?")
    public void warmUpCache() {
        log.info("Timed task of '/warmUpCache'");
        new Thread(new CachePrewarmThread()).start();
    }


    @GetMapping("/productInfo")
    public ApiResponse productInfo(Long productId) {
        ApiResponse<Object> res = new ApiResponse<>();
        ProductInfoCommand productInfoCommand = new ProductInfoCommand(productId);
        ProductInfo productInfo = productInfoCommand.execute();

        for (int i = 1; i < 100; i++) {
            int id = i % 5;
            ProductInfoCommand productInfoCommond = new ProductInfoCommand(Long.valueOf(id));
            productInfoCommond.execute();
        }
        return res;
    }

    //  =============================================================
    //  ======================== 以下都是测试用例 ====================
    //  =============================================================
    @PostMapping("/testPutCache")
    public ApiResponse testPutCache(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        cacheService.saveProductInfo2LocalCache(productInfo);
        return response;
    }

    @PostMapping("/testGetCache")
    public ApiResponse testGetCache(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        ProductInfo fromLocalCache = cacheService.getProductInfoFromLocalCache(productInfo.getId());
        response.setData(fromLocalCache);
        return response;
    }

    @PostMapping("/testRedisPut")
    public ApiResponse testRedisPut(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            cacheService.saveProductInfo2ReidsCache(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(e.getMessage());
        }
        return response;
    }

    @PostMapping("/testRedisGet")
    public ApiResponse testRedisGet(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            ProductInfo productInfoFromRedisCache = cacheService.getProductInfoFromRedisCache(productInfo.getId());
            response.setData(productInfoFromRedisCache);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(e.getMessage());
        }
        return response;
    }

    @GetMapping("/testRedisProductInfoDel")
    public ApiResponse testRedisProductInfoDel(Long productId) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            cacheService.delProductInfoFromReidsCache(productId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(e.getMessage());
        }
        return response;
    }

    @GetMapping("/testRedisShopInfoDel")
    public ApiResponse testRedisShopInfoDel(Long shopId) {
        ApiResponse<Object> response = new ApiResponse<>();
        try {
            cacheService.delShopInfoFromReidsCache(shopId);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(e.getMessage());
        }
        return response;
    }

}
