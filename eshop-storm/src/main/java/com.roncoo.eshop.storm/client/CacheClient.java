package com.roncoo.eshop.storm.client;


import lombok.Data;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Data
public class CacheClient {

    private  String baseUrl;

    private  CacheService cacheService;

    public CacheClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.cacheService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(CacheService.class);
    }
}
