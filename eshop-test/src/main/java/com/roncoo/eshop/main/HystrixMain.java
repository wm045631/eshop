package com.roncoo.eshop.main;

import com.roncoo.eshop.hystrix.ProductInfoCommand;
import com.roncoo.eshop.mapper.model.ProductInfo;

public class HystrixMain {
    public static void main(String[] args) throws InterruptedException {

        int id = 1;
        ProductInfoCommand productInfoCommond = new ProductInfoCommand(Long.valueOf(id));
        ProductInfo productInfo = productInfoCommond.execute();
        System.out.println(productInfo);

//        for (int i = 1; i < 3; i++) {
//            int id = i % 5;
//            ProductInfoCommand productInfoCommond = new ProductInfoCommand(Long.valueOf(id));
//            ProductInfo productInfo = productInfoCommond.execute();
//            System.out.println(productInfo);
//        }

        Thread.sleep(10000);
        System.out.println("i am over");
    }
}

