package com.roncoo.eshop.cache.zk;

import com.roncoo.eshop.cache.config.ZookeeperConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ZookeeperDistributedLock {

    @Autowired
    private ZooKeeper zookeeper;

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    /**
     * 获取分布式锁
     */
    public void lock(String path) {
        try {
            zookeeper.create(path,
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
            log.info("success to acquire lock for path ={}", path);
        } catch (Exception e) {
            // 如果那个商品对应的锁的node，已经存在了，就是已经被别人加锁了，那么就这里就会报错
            // NodeExistsException。循环尝试获取锁
            int count = 0;
            while (true) {
                try {
                    Thread.sleep(200);
                    zookeeper.create(path,
                            "".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.EPHEMERAL);
                } catch (Exception e2) {
//                    e2.printStackTrace();
                    count++;
                    log.info("try to {} times to acquire lock for path = {} ", count, path);
                    continue;
                }
                log.info("success to acquire lock for path = {} after {} times try......", path, count);
                break;
            }
        }
    }

    /**
     * 释放掉一个分布式锁
     */
    public void unlock(String path) {
//        String path = zookeeperConfig.getLockPrefix() + productId;
        try {
            zookeeper.delete(path, -1);
            log.info("release distributed lock. path = {}", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
