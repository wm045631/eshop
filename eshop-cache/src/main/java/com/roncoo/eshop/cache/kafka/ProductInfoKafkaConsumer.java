package com.roncoo.eshop.cache.kafka;

import com.roncoo.eshop.cache.config.KafkaConfig;
import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.mapper.ShopInfoMapper;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.zk.ZookeeperDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ProductInfoKafkaConsumer implements InitializingBean {

    @Value("${kafka.consumer.cache.topic:cache-message}")
    private String topic;

    @Value("${kafka.consumer.cache.groupid:eshop-cache-group}")
    private String groupId;

    @Value("${kafka.consumer.cache.nthread:3}")
    private int nthread;

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ShopInfoMapper shopInfoMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private ZookeeperDistributedLock zookeeperDistributedLock;

    @Override
    public void afterPropertiesSet() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(nthread);
        // 这里针对每个线程，都创建一个KafkaConsumer
        for (int i = 0; i < nthread; i++) {
            executor.submit(new KafkaMessageProcess(kafkaConfig,
                    groupId,
                    topic,
                    cacheService,
                    shopInfoMapper,
                    productInfoMapper,
                    zookeeperDistributedLock));
        }
    }
}
