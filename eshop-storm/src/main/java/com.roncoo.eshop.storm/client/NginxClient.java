package com.roncoo.eshop.storm.client;

import lombok.Data;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import java.util.ArrayList;
import java.util.List;

@Data
public class NginxClient {
    private String appNginxUrl;
    private String distributeNginxUrl;
    private DistributeNginxService distributeNginxService;
    private List<AppNginxService> appNginxServiceList = new ArrayList<>();

    public NginxClient(String appNginxUrl, String distributeNginxUrl) {
        this.appNginxUrl = appNginxUrl;
        this.distributeNginxUrl = distributeNginxUrl;

        this.distributeNginxService = new Retrofit.Builder()
                .baseUrl("http://" + distributeNginxUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(DistributeNginxService.class);

        for (String uri : appNginxUrl.split(",")) {
            System.out.println("DEBUG: http://" + uri);
            AppNginxService appNginxService = new Retrofit.Builder()
                    .baseUrl("http://" + uri)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build().create(AppNginxService.class);
            appNginxServiceList.add(appNginxService);
        }
    }
}
