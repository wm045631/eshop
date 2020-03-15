package com.roncoo.eshop.cache.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Getter
@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperConfig {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    @Value("${zookeeper.server}")
    private String server;
    @Value("${zookeeper.sessiontimeout}")
    private int sessiontimeout;
    @Value("${zookeeper.retry}")
    private int retry;
    @Value("${zookeeper.lockPrefix}")
    private String lockPrefix;

    @Bean
    public ZooKeeper createZk() {
        // 去连接zookeeper server，创建会话的时候，是异步去进行的
        // 所以要给一个监听器，说告诉我们什么时候才是真正完成了跟zk server的连接
        ZooKeeper zookeeper = null;
        int count = 0;
        while (count < retry) {
            try {
                zookeeper = new ZooKeeper(
                        server,
                        sessiontimeout,
                        new ZooKeeperWatcher());
                // 给一个状态CONNECTING，连接中
                log.info("zookeeper state = {}", zookeeper.getState());
                try {
                    connectedSemaphore.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("ZooKeeper session established......");
                return zookeeper;
            } catch (Exception e) {
                if (count == retry) {
                    e.printStackTrace();
                }
                count++;
            }
        }
        return zookeeper;
    }

    /**
     * 建立zk session的watcher
     *
     * @author Administrator
     */
    private class ZooKeeperWatcher implements Watcher {

        public void process(WatchedEvent event) {
            System.out.println("Receive watched event: " + event.getState());
            if (Event.KeeperState.SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
