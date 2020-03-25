package com.roncoo.eshop.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.roncoo.eshop.http.HttpClientUtils;
import com.roncoo.eshop.mapper.model.ProductInfo;

public class ProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public ProductInfoCommand(Long productId) {
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        String url = "http://cdh1:8081/ehcache/getProductInfo?productId=" + productId;
        String res = HttpClientUtils.sendGetRequest(url);
        return JSONObject.parseObject(res, ProductInfo.class);
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
