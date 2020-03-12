package com.roncoo.eshop.cache.controller;

import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.pojo.ApiResponse;
import com.roncoo.eshop.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ehcache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @PostMapping("/testPutCache")
    public ApiResponse testPutCache(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        cacheService.saveToLocalCache(productInfo);
        return response;
    }

    @PostMapping("/testGetCache")
    public ApiResponse testGetCache(@RequestBody ProductInfo productInfo) {
        ApiResponse<Object> response = new ApiResponse<>();
        ProductInfo fromLocalCache = cacheService.getFromLocalCache(productInfo.getId());
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
}
