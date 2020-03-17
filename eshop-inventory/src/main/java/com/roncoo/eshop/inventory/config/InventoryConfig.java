package com.roncoo.eshop.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "inventory.request-process")
public class InventoryConfig {
    private int nThread;
    private int queueCapacity;
    private int queryTimeout;
}
