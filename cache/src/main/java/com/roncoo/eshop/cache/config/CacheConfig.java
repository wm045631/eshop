package com.roncoo.eshop.cache.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 缓存配置管理类
 */
@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public EhCacheManagerFactoryBean createEhCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        factoryBean.setShared(true);
        return factoryBean;
    }
    @Bean
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean factoryBean){
        return new EhCacheCacheManager(factoryBean.getObject());
    }
}
