package com.roncoo.eshop.storm.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistributeNginxService {
    @GET("/hot")
    Call<Void> setHotProductId(@Query("productId") Long productId);

    @GET("/cancelHot")
    Call<Void> cancelHot(@Query("productId") Long productId);
}
