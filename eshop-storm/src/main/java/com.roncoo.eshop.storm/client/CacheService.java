package com.roncoo.eshop.storm.client;

import com.roncoo.eshop.storm.model.ProductInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CacheService {
    @GET("/ehcache/getProductInfo")
    Call<ProductInfo> getProductInfo(@Query("productId") Long productId);
}
