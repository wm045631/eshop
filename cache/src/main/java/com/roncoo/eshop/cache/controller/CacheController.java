package com.roncoo.eshop.cache.controller;

import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.mapper.ShopInfoMapper;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ProductInfoExample;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.model.ShopInfoExample;
import com.roncoo.eshop.cache.pojo.ApiResponse;
import com.roncoo.eshop.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ehcache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private ShopInfoMapper shopInfoMapper;

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
        log.info("get product info by productId = {}", productId);
        ProductInfo productInfo = null;

        // 从redis中获取
        productInfo = cacheService.getProductInfoFromRedisCache(productId);

        if (productInfo == null) {
            log.info("get product info from redis failed. productId = {}", productId);
            // 从ehcache中获取
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
        } else {
            log.info("get product info from redis success. productId = {}", productId);
            return productInfo;
        }
        // todo: redis和ehcache都没有。需要重新从mysql获取数据，重建缓存  --- 存在并发冲突的问题
        if (productInfo == null) {
            log.info("get product info from ehcache failed. productId = {}", productId);
            ProductInfoExample example = new ProductInfoExample();
            ProductInfoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(productId);
            List<ProductInfo> productInfos = productInfoMapper.selectByExample(example);
            if (productInfos != null && productInfos.size() == 1) {
                productInfo = productInfos.get(0);
                cacheService.saveProductInfo2LocalCache(productInfo);
                cacheService.saveProductInfo2ReidsCache(productInfo);
            }
        } else {
            log.info("get product info from ehcache success. productId = {}", productId);
        }
        log.info("get product info. productId = {}, productInfo = {}", productId, productInfo);
        return productInfo;
    }

    @GetMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        log.info("get shop info by shopId = {}", shopId);
        ShopInfo shopInfo = null;
        // 从redis中获取
        shopInfo = cacheService.getShopInfoFromRedisCache(shopId);

        if (shopInfo == null) {
            log.info("get shop info info from redis failed. shopId = {}", shopId);
            // 从ehcache中获取
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
        } else {
            log.info("get shop info from redis success. shopId = {}", shopId);
            return shopInfo;
        }
        // todo: redis和ehcache都没有。需要重新从mysql获取数据，重建缓存
        if (shopInfo == null) {
            log.info("get shop info info from ehcache failed. shopId = {}", shopId);
            ShopInfoExample example = new ShopInfoExample();
            ShopInfoExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(shopId);
            List<ShopInfo> shopInfos = shopInfoMapper.selectByExample(example);
            if (shopInfos != null && shopInfos.size() == 1) {
                shopInfo = shopInfos.get(0);
                cacheService.saveShopInfo2LocalCache(shopInfo);
                cacheService.saveShopInfo2ReidsCache(shopInfo);
            }
        } else {
            log.info("get shop info from ehcache success. productId = {}", shopId);
        }
        log.info("get product info. shopId = {}, shopInfo = {}", shopId, shopInfo);
        return shopInfo;
    }

    //  =============================================================
    //  ======================== 以下都是用例 =======================
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
