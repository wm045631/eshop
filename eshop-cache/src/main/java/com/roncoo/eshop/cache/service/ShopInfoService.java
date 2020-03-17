package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;

public interface ShopInfoService {

    ShopInfo getShopInfoById(Long shopId);

}
