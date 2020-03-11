package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;

public interface CacheService {
    /**
     * 将商品信息保存到本地缓存
     * @param productInfo
     * @return
     */
    ProductInfo saveToLocalCache(ProductInfo productInfo);

    ProductInfo getFromLocalCache(Long productId);

}
