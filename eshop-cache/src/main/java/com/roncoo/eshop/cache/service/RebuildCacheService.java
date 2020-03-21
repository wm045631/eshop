package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.constant.LockPrefix;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Service
public class RebuildCacheService implements InitializingBean {

    private BlockingDeque rebuildCacheQueue = new LinkedBlockingDeque(1000);

    @Autowired
    private ZookeeperSession zookeeperSession;

    @Autowired
    private CacheService cacheService;

    public void addMsg(Object o) {
        try {
            rebuildCacheQueue.put(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存被动重建。从rebuildCacheQueue中获取数据
     */
    @Override
    public void afterPropertiesSet() {
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                try {
                    Object obj = rebuildCacheQueue.take();
                    if (obj instanceof ProductInfo) {
                        ProductInfo productInfo = (ProductInfo) obj;
                        log.info("get msg from rebuildCacheQueue. {}", productInfo);
                        zookeeperSession.lock(LockPrefix.PRODUCT_LOCK_PREFIX + productInfo.getId());
                        //  成功获取到锁后，先从redis获取数据，比较update_time,再判断是否更新redis数据
                        ProductInfo proCache = cacheService.getProductInfoFromRedisCache(productInfo.getId());
                        if (proCache == null
                                || proCache.getUpdateTime() == null
                                || productInfo.getUpdateTime().after(proCache.getUpdateTime())) {
                            log.info("rebuild productInfo in redis.{}", productInfo.toString());
                            cacheService.saveProductInfo2ReidsCache(productInfo);
                        } else {
                            log.info("product info in redis is not after this record, in redis: {}", cacheService.getProductInfoFromRedisCache(productInfo.getId()).toString());
                        }
                        zookeeperSession.unlock(LockPrefix.PRODUCT_LOCK_PREFIX + productInfo.getId());
                    } else if (obj instanceof ShopInfo) {
                        ShopInfo shopInfo = (ShopInfo) obj;
                        log.info("get msg from rebuildCacheQueue. {}", shopInfo);
                        zookeeperSession.lock(LockPrefix.SHOP_LOCK_PREFIX + shopInfo.getId());
                        //  成功获取到锁后，先从redis获取数据，比较update_time,再判断是否更新redis数据
                        ShopInfo shopCache = cacheService.getShopInfoFromRedisCache(shopInfo.getId());
                        if (shopCache == null
                                || shopCache.getUpdateTime() == null
                                || shopInfo.getUpdateTime().after(shopCache.getUpdateTime())) {
                            log.info("rebuild shopInfo in redis.{}", shopInfo.toString());
                            cacheService.saveShopInfo2ReidsCache(shopInfo);
                        } else {
                            log.info("shop info in redis is after this record, in redis: {}", cacheService.getShopInfoFromRedisCache(shopInfo.getId()).toString());
                        }
                        zookeeperSession.unlock(LockPrefix.SHOP_LOCK_PREFIX + shopInfo.getId());
                    } else {
                        log.warn("can not handle this message in rebuildCacheQueue. object = {}", obj);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
