package com.roncoo.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.config.RedisKeys;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private JedisCluster jedisCluster;
    /**
     * 指定使用的缓存策略。xml配置中name=local的配置
     */
    public static final String CACHE_NAME = "local";

//    @Override
//    @CachePut(value = CACHE_NAME, key = "'key_'+#productInfo.getId()")
//    public ProductInfo saveToLocalCache(ProductInfo productInfo) {
//        return productInfo;
//    }
//
//    @Override
//    @Cacheable(value = CACHE_NAME, key = "'key_'+#id")
//    public ProductInfo getFromLocalCache(Long id) {
//        // 如果没有取到，就返回null。取到了直接返回了
//        return null;
//    }

    /**
     * 将商品信息保存到redis中
     */
    public void saveProductInfo2ReidsCache(ProductInfo productInfo) {
        String key = RedisKeys.PRODUCT_INFO_KEY + productInfo.getId();
//        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set(key, JSONObject.toJSONString(productInfo));
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        String key = RedisKeys.PRODUCT_INFO_KEY + productId;
//        String s = jedisCluster.get(key);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String s = operations.get(key);
        return JSONObject.parseObject(s, ProductInfo.class);
    }

    @Override
    public void delProductInfoFromReidsCache(Long productId) {
        String key = RedisKeys.PRODUCT_INFO_KEY + productId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 将商品信息保存到本地的ehcache缓存中
     */
    @CachePut(value = CACHE_NAME, key = "'product_info_'+#productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    /**
     * 从本地ehcache缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'product_info_'+#productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    /**
     * 将店铺信息保存到本地的ehcache缓存中
     */
    @CachePut(value = CACHE_NAME, key = "'shop_info_'+#shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    /**
     * 从本地ehcache缓存中获取店铺信息
     */
    @Cacheable(value = CACHE_NAME, key = "'shop_info_'+#shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        String key = RedisKeys.SHOP_INFO_KEY + shopId;
        try {
//            String s = jedisCluster.get(key);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String s = operations.get(key);
            if (s == null || "".equals(s.trim())) return null;
            return JSONObject.parseObject(s, ShopInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delShopInfoFromReidsCache(Long shopId) {
        String key = RedisKeys.SHOP_INFO_KEY + shopId;
        stringRedisTemplate.delete(key);
    }

    /**
     * 将店铺信息保存到redis中
     */
    public void saveShopInfo2ReidsCache(ShopInfo shopInfo) {
        String key = RedisKeys.SHOP_INFO_KEY + shopInfo.getId();
//        jedisCluster.set(key, JSONObject.toJSONString(shopInfo));
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set(key, JSONObject.toJSONString(shopInfo));
    }
}
