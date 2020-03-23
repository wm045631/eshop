package com.roncoo.eshop.storm.client;

import com.roncoo.eshop.storm.model.ProductInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppNginxService {
    @POST("/hotProduct")
    Call<Void> setHotProductInfo(@Body ProductInfo productInfo);
}
