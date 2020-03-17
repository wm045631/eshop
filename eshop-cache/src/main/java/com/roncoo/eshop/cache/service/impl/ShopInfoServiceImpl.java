package com.roncoo.eshop.cache.service.impl;

import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.mapper.ShopInfoMapper;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ProductInfoExample;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.model.ShopInfoExample;
import com.roncoo.eshop.cache.service.ProductInfoService;
import com.roncoo.eshop.cache.service.ShopInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ShopInfoServiceImpl implements ShopInfoService {

    @Autowired
    private ShopInfoMapper shopInfoMapper;

    @Override
    public ShopInfo getShopInfoById(Long shopId) {
        ShopInfo shopInfo = null;
        ShopInfoExample example = new ShopInfoExample();
        ShopInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(shopId);
        List<ShopInfo> infos = shopInfoMapper.selectByExample(example);
        if (infos != null && infos.size() == 1) {
            shopInfo = infos.get(0);
        }
        return shopInfo;
    }
}
