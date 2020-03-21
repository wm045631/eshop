package com.roncoo.eshop.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.config.KafkaConfig;
import com.roncoo.eshop.cache.constant.LockPrefix;
import com.roncoo.eshop.cache.mapper.ProductInfoMapper;
import com.roncoo.eshop.cache.mapper.ShopInfoMapper;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ProductInfoExample;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.model.ShopInfoExample;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Data
@Slf4j
public class KafkaMessageProcess implements Runnable {

    private KafkaConfig kafkaConfig;
    private String groupId;
    private String topic;
    private CacheService cacheService;
    private ShopInfoMapper shopInfoMapper;
    private ProductInfoMapper productInfoMapper;
    private ZookeeperSession zookeeperSession;

    public KafkaMessageProcess(KafkaConfig kafkaConfig,
                               String groupId,
                               String topic,
                               CacheService cacheService,
                               ShopInfoMapper shopInfoMapper,
                               ProductInfoMapper productInfoMapper,
                               ZookeeperSession zookeeperSession) {
        this.kafkaConfig = kafkaConfig;
        this.groupId = groupId;
        this.topic = topic;
        this.cacheService = cacheService;
        this.productInfoMapper = productInfoMapper;
        this.shopInfoMapper = shopInfoMapper;
        this.zookeeperSession = zookeeperSession;
    }

    /**
     * 从kafka消费数据，进行处理
     * kafka-console-producer --broker-list cdh1:9092,cdh2:9092,cdh3:9092 --topic cache-message
     * {"serviceId":"productInfoService","productId":1}
     * {"serviceId":"shopInfoService","shopId":1}
     */
    @Override
    public void run() {
        Properties props = new Properties();
        kafkaConfig.defaultConsumerConfigs(groupId).forEach(props::put);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    log.info("topic:{},route_key:{},partition:{},offset:{},message:{}",
                            record.topic(), record.key(), record.partition(), record.offset(), message);

                    // 首先将message转换成json对象
                    JSONObject messageJSONObject = JSONObject.parseObject(message);

                    // 从这里提取出消息对应的服务的标识
                    String serviceId = messageJSONObject.getString("serviceId");

                    // 如果是商品信息服务
                    if ("productInfoService".equals(serviceId)) {
                        processProductInfoChangeMessage(messageJSONObject);
                    } else if ("shopInfoService".equals(serviceId)) {
                        processShopInfoChangeMessage(messageJSONObject);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 处理商品信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景
        ProductInfoExample example = new ProductInfoExample();
        ProductInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(productId);
        List<ProductInfo> productInfos = productInfoMapper.selectByExample(example);
        if (productInfos != null && productInfos.size() == 1) {
            ProductInfo productInfoInMysql = productInfos.get(0);
            cacheService.saveProductInfo2LocalCache(productInfoInMysql);
            ProductInfo info = cacheService.getProductInfoFromLocalCache(productId);
            log.info("获取刚保存到本地缓存的商品信息: {}", info.toString());
            // todo:在将数据写入redis之前，应该获取zk分布式锁
            zookeeperSession.lock(LockPrefix.PRODUCT_LOCK_PREFIX + productId);
            /**
             * 测试缓存更新和缓存重建都尝试获得相同的分布式锁。
             * 一边往kafka发送{"serviceId":"productInfoService","productId":1}  --> 缓存主动更新
             * 一边http://localhost:8081/ehcache/getProductInfo?productId=1，让redis和ehcache返回null。--> 缓存被动重建
             */
            //            try {
            //                Thread.sleep(20000);
            //            } catch (InterruptedException e) {
            //                e.printStackTrace();
            //            }
            //  成功获取到锁后，先从redis获取数据，比较update_time,再判断是否更新redis数据
            ProductInfo productInfoFromRedisCache = cacheService.getProductInfoFromRedisCache(productId);
            if (productInfoFromRedisCache == null
                    || productInfoFromRedisCache.getUpdateTime() == null
                    || productInfoInMysql.getUpdateTime().after(productInfoFromRedisCache.getUpdateTime())) {
                log.info("update product info in redis.{}", productInfoInMysql.toString());
                cacheService.saveProductInfo2ReidsCache(productInfoInMysql);
                log.info("获取刚保存到redis的商品信息: {}", cacheService.getProductInfoFromRedisCache(productId).toString());
            } else {
                log.info("product info in redis is after this record, in redis: {}", cacheService.getProductInfoFromRedisCache(productId).toString());
            }
            zookeeperSession.unlock(LockPrefix.PRODUCT_LOCK_PREFIX + productId);
        } else {
            log.warn("can not get product_info by id = {}", productId);
        }
    }

    /**
     * 处理店铺信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

//        String shopInfoJSON = "{\"id\": 1, \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
//        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);

        ShopInfoExample example = new ShopInfoExample();
        ShopInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(shopId);
        List<ShopInfo> shopInfos = shopInfoMapper.selectByExample(example);
        if (shopInfos != null && shopInfos.size() == 1) {
            ShopInfo shopInfoInMysql = shopInfos.get(0);
            cacheService.saveShopInfo2LocalCache(shopInfoInMysql);
            log.info("获取刚保存到本地缓存的店铺信息：" + cacheService.getShopInfoFromLocalCache(shopId));
            // todo:在将数据写入redis之前，应该获取zk分布式锁
            zookeeperSession.lock(LockPrefix.SHOP_LOCK_PREFIX + shopId);
            try {
                //  成功获取到锁后，先从redis获取数据，比较update_time,再判断是否更新redis数据
                ShopInfo shopInfoFromRedisCache = cacheService.getShopInfoFromRedisCache(shopId);
                if (shopInfoFromRedisCache == null
                        || shopInfoFromRedisCache.getUpdateTime() == null
                        || shopInfoInMysql.getUpdateTime().after(shopInfoFromRedisCache.getUpdateTime())) {
                    log.info("update shop info in redis.{}", shopInfoInMysql.toString());
                    cacheService.saveShopInfo2ReidsCache(shopInfoInMysql);
                    log.info("获取刚保存到redis的商品信息: {}", cacheService.getShopInfoFromRedisCache(shopId).toString());
                } else {
                    log.info("shop info in redis is after this record, in redis: {}", cacheService.getShopInfoFromRedisCache(shopId).toString());
                }
                zookeeperSession.unlock(LockPrefix.SHOP_LOCK_PREFIX + shopId);
            } catch (Exception e) {
                zookeeperSession.unlock(LockPrefix.SHOP_LOCK_PREFIX + shopId);
            }
        } else {
            log.warn("can not get shop_info by id = {}", shopId);
        }
    }
}
