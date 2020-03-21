package com.roncoo.eshop.storm.zk;


import com.roncoo.eshop.storm.spout.AccessLogKafkaSpout;
import lombok.Data;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Data
public class ZookeeperConfig {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperConfig.class);

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private String server;
//    private int sessiontimeout = 50000;
//    private int retry = 3;
    private ZooKeeper zooKeeper;

    public ZookeeperConfig() {
        Yaml yaml = new Yaml();
        InputStream in = AccessLogKafkaSpout.class.getClassLoader().getResourceAsStream("kafka_config.yaml");
        Map<String, String> config = yaml.loadAs(in, Map.class);
        this.server = config.get("zookeeper_server");

//        this.sessiontimeout = Integer.parseInt(config.get("zookeeper_sessiontimeout"));
//        this.retry = Integer.parseInt(config.get("zookeeper_retry"));
        this.zooKeeper = createZk();
    }


    public static ZookeeperConfig getInstance() {
        return Singleton.getInstance();
    }

    public ZooKeeper createZk() {
        // 去连接zookeeper server，创建会话的时候，是异步去进行的
        // 所以要给一个监听器，说告诉我们什么时候才是真正完成了跟zk server的连接
        ZooKeeper zookeeper = null;
        int count = 0;
        while (count < 3) {
            try {
                zookeeper = new ZooKeeper(
                        server,
                        50000,
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
                if (count == 3) {
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

    private static class Singleton {
        private static ZookeeperConfig instance = null;

        static {
            instance = new ZookeeperConfig();
        }

        public static ZookeeperConfig getInstance() {
            return instance;
        }
    }
}
