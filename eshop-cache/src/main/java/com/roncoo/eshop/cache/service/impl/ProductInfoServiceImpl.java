package com.roncoo.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.roncoo.eshop.cache.hystrix.HttpClientUtils;
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

    @HystrixCommand(
            fallbackMethod = "productInfoFailed",
            // commandKey：唯一表示该方法，对应请求底层依赖服务的一个接口
            commandKey = "getProductByHystrix",
            // groupKey: 代表一个底层依赖服务，对应该依赖服务的多个接口
            groupKey = "ProductGroup",
            // threadPoolKey对应的线程池
            threadPoolKey = "ProductThreadPool",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            })
    public ProductInfo getProductByHystrix(String productId) {
        log.info("getProductInfo by hystrix id = {}", productId);
        String url = "http://cdh1:8081/ehcache/getProductInfo?productId=" + productId;
        String res = HttpClientUtils.sendGetRequest(url);
        return JSONObject.parseObject(res, ProductInfo.class);
    }

    @HystrixCommand
    private ProductInfo productInfoFailed(String productId) {
        String s = "get productInfo Failed, productId = " + productId;
        log.info(s);
        return null;
    }
}
