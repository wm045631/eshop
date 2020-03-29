package com.roncoo.eshop.cache;

import com.roncoo.eshop.cache.hystrix.HttpClientUtils;

public class SentinelTest {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println("START:  " + i);
            String url = "http://localhost:8081/ehcache/getProductInfosBySentinel?productIds=1,2,3";
            String res = HttpClientUtils.sendGetRequest(url);
            System.out.println("res = " + res);
        }
    }
}
