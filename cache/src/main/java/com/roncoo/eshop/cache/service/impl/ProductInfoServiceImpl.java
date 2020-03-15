package com.roncoo.eshop.cache.service.impl;

import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ProductInfoExample;
import com.roncoo.eshop.cache.service.ProductInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Override
    public ProductInfo getProductInfoById(Long productId) {
        ProductInfo productInfo = null;
        ProductInfoExample example = new ProductInfoExample();
        ProductInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(productId);
        List<ProductInfo> productInfos = productInfoMapper.selectByExample(example);
        if (productInfos != null && productInfos.size() == 1) {
            productInfo = productInfos.get(0);
        }
        return productInfo;
    }
}
