package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;

public interface CacheService {
    // productInfo.ehcache
    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    ProductInfo getProductInfoFromLocalCache(Long productId);

    // productInfo.redis
    void saveProductInfo2ReidsCache(ProductInfo productInfo);

    ProductInfo getProductInfoFromRedisCache(Long productId);

    // shopInfo.ehcache
    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    ShopInfo getShopInfoFromLocalCache(Long shopId);

    // shopInfo.redis
    void saveShopInfo2ReidsCache(ShopInfo shopInfo);

    ShopInfo getShopInfoFromRedisCache(Long shopId);
}
