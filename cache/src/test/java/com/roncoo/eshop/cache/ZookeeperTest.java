package com.roncoo.eshop.cache;

import com.roncoo.eshop.cache.config.ZookeeperConfig;
import com.roncoo.eshop.cache.zk.ZookeeperDistributedLock;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(classes = {ZookeeperConfig.class, ZookeeperDistributedLock.class})
//开启自动配置，排除springjdbc自动配置
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class ZookeeperTest {

    @Autowired
    private ZooKeeper zookeeper;

    @Autowired
    private ZookeeperDistributedLock zookeeperDistributedLock;

    @Test
    public void test() throws InterruptedException {
        System.out.println("zookeeper" + zookeeper);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Long productId = 1L;
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    zookeeperDistributedLock.acquireDistributedLock(productId);
                    Thread.sleep(1);
                    zookeeperDistributedLock.releaseDistributedLock(productId);
                } catch (Exception e) {
                    System.out.println("NodeExists ");
                    e.printStackTrace();
                }
            }).start();
            countDownLatch.countDown();
        }
        Thread.sleep(20000);
        System.out.println("-- over --");
    }
}
