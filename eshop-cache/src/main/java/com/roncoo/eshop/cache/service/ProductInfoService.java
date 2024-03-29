package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;

public interface ProductInfoService {

    ProductInfo getProductInfoById(Long productId);

    ProductInfo getProductByHystrix(String productId);

    ProductInfo getProductInfosBySentinel(String productId);
}
