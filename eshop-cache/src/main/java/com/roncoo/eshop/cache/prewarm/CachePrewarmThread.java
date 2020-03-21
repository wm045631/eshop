package com.roncoo.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.constant.LockPrefix;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.service.ProductInfoService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存预热线程，从zk读取taskid的列表，然后获取taskid对应的nodeData
 */
@Slf4j
public class CachePrewarmThread implements Runnable {
    @Override
    public void run() {
        ZookeeperSession zkSession = (ZookeeperSession) SpringContext.getApplicationContext().getBean("zookeeperSession");
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheServiceImpl");
        ProductInfoService productInfoService = (ProductInfoService) SpringContext.getApplicationContext().getBean("productInfoServiceImpl");

        log.info("************* Cache server is starting. begin to warm up *************");
        String taskidList = zkSession.getDataNode(LockPrefix.DATA_PATH);
        log.info("Get taskid list from zk. taskidList = {}", taskidList);

        if (taskidList != null && !"".equals(taskidList)) {
            String[] taskids = taskidList.split(",");
            for (String taskid : taskids) {
                // 基于双重zookeeper分布式锁完成分布式并行缓存预热
                boolean acquireOncelock = zkSession.acquireOncelock(LockPrefix.TASK_ID_LOCK_PREFIX + taskid);
                if (!acquireOncelock) {
                    log.warn("other thread is warming up.taskid = {}", taskid);
                    continue;
                } else {
                    // 一旦获取到锁，该线程负责缓存预热
                    // 检查该taskid缓存预热的状态
                    zkSession.lock(LockPrefix.TASK_ID_STATUS_LOCK_PREFIX + taskid);
                    String taskidStatus = zkSession.getDataNode(LockPrefix.TASK_ID_STATUS_DATA_PREFIX + taskid);
                    if (!"true".equals(taskidStatus)) {
                        log.info("begin to warm-up. taskid = {}", taskid);
                        // 该线程开始预热，从zk读取productId的list数据
                        String productidList = zkSession.getDataNode(LockPrefix.HOT_PRODUCT_PREFIX + taskid);
                        JSONArray productidArray = JSONArray.parseArray(productidList);
                        for (int i = 0; i < productidArray.size(); i++) {
                            JSONObject productidJson = (JSONObject) productidArray.get(0);
                            // 从数据库查询，写入ehcache和redis
                            ProductInfo productInfoById = productInfoService.getProductInfoById(productidJson.getLong("key"));
                            cacheService.saveProductInfo2LocalCache(productInfoById);
                            cacheService.saveProductInfo2ReidsCache(productInfoById);
                            // 修改taskid对应的预热状态：true
                            zkSession.setDataNode(LockPrefix.TASK_ID_STATUS_DATA_PREFIX + taskid, "true");
                        }
                    } else {
                        log.info("The status is true for {}. do not warm-up again", LockPrefix.TASK_ID_STATUS_LOCK_PREFIX + taskid);
                    }
                    zkSession.unlock(LockPrefix.TASK_ID_STATUS_LOCK_PREFIX + taskid);
                    zkSession.unlock(LockPrefix.TASK_ID_LOCK_PREFIX + taskid);
                }
            }
        }
    }
}
