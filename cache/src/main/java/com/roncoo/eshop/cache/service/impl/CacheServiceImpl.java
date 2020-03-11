package com.roncoo.eshop.cache.service.impl;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {
    /**
     * 指定使用的缓存策略。xml配置中name=local的配置
     */
    public static final String CACHE_NAME = "local";

    @Override
    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
    public ProductInfo saveToLocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
    public ProductInfo getFromLocalCache(Long id) {
        // 如果没有取到，就返回null。取到了直接返回了
        return null;
    }
}
