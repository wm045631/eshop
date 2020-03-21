package com.roncoo.eshop.storm.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperSession {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperSession.class);

    private ZooKeeper zookeeper;

    public ZookeeperSession() {
//        ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
        ZookeeperConfig zookeeperConfig = ZookeeperConfig.getInstance();
        this.zookeeper = zookeeperConfig.getZooKeeper();
        log.info("init zookeeper distributedLock success.");
    }

    public String getDataNode(String path) {
        try {
            return new String(zookeeper.getData(path, null, new Stat()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setDataNode(String path, String data) {
        try {
            log.info("BGN: write data to zk node. data = {}, path = {}", data, path);
            Stat exists = zookeeper.exists(path, false);
            if (exists == null) {
                zookeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zookeeper.setData(path, data.getBytes(), -1);
            }
            log.info("SUCCESS: write data to zk node. data = {}, path = {}", data, path);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("FAILURE: write data to zk node. data = {}, path = {}", data, path);
        }
    }

    public static ZookeeperSession getInstance() {
        return Singleton.getInstance();
    }

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
                    Thread.sleep(20);
                    zookeeper.create(path,
                            "".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.EPHEMERAL);
                } catch (Exception e2) {
                    count++;
//                    log.info("try to {} times to acquire lock for path = {} ", count, path);
                    continue;
                }
                log.info("success to acquire lock for path = {} after {} times try......", path, count);
                break;
            }
        }
    }

    /**
     * 获取分布式锁
     * <p>
     * 如果失败，不再尝试重新获取。redis从zk获取热点数据的时候，如果获取锁失败，快速返回，因为其他线程正在进行缓存预热，
     */
    public boolean acquireOncelock(String path) {
        try {
            zookeeper.create(path,
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
            log.info("success to acquire lock for path ={}", path);
            return true;
        } catch (Exception e) {
            log.info("failed to acquire lock for path ={}", path);
        }
        return false;
    }

    /**
     * 释放掉一个分布式锁
     */
    public void unlock(String path) {
        try {
            zookeeper.delete(path, -1);
            log.info("release distributed lock. path = {}", path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class Singleton {
        private static ZookeeperSession instance = null;

        static {
            instance = new ZookeeperSession();
        }

        public static ZookeeperSession getInstance() {
            return instance;
        }
    }
}
