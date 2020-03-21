package com.roncoo.eshop.storm.bolt;

import com.alibaba.fastjson.JSONArray;
import com.roncoo.eshop.storm.zk.ZkConst;
import com.roncoo.eshop.storm.zk.ZookeeperSession;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HotProductCountBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(HotProductCountBolt.class);

    // 根据内存和数据量计算的容量。采用LRUMap
    private LRUMap<Long, Long> productCountMap = new LRUMap<Long, Long>(1000);

    private int taskid;
    private ZookeeperSession zkSession;

    /**
     * 针对LRUmap进行清理
     *
     * @param stormConf
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        zkSession = ZookeeperSession.getInstance();
        this.taskid = context.getThisTaskId();
        log.info("my taskis is {}", this.taskid);
        // 每隔一分钟，统计一下topN
        new Thread(new ProductCountThread()).start();

        // 1、将自己的taskId写入一个zookeeper节点中，形成taskId列表
        // 2、然后将自己的topN列表，写入自己taskId对应的zookeeper节点
        // 3、并行的预热程序就能从第一步中知道有哪些taskId
        // 4、并行预热程序根据每个taskId去获取一个锁，然后从对应的node中拿到热门商品数据
        initTaskid(this.taskid);
    }

    /**
     * 所有的task启动的时候，执行到HotProductCountBolt，都会将自己的taskid写入到"/taskid-list"节点的Data中
     * 格式用逗号分隔。
     * <p>
     * 写入的时候，需要使用分布式锁
     *
     * @param taskid
     */
    public void initTaskid(int taskid) {
        zkSession.lock(ZkConst.TASK_ID_LIST_LOCK);
        // todo:如果是task重新提交，ZkConst.DATA_PATH下记录的旧的taskid调用cleanup清除
        String taskIdList = zkSession.getDataNode(ZkConst.DATA_PATH);
        log.info("Get taskids form zk, taskIdList = {}", taskIdList);
        Set<String> taskidSet = new HashSet<>();
        if (!"".equals(taskIdList)) {
            String[] taskids = taskIdList.split(",");
            for (String id : taskids) {
                taskidSet.add(id);
            }
        }
        taskidSet.add(taskid + "");
        taskIdList = String.join(",", taskidSet);
        zkSession.setDataNode(ZkConst.DATA_PATH, taskIdList);

        zkSession.unlock(ZkConst.TASK_ID_LIST_LOCK);
    }

    /**
     * Cleanup方法当一个IBolt即将关闭时被调用。
     * 不能保证cleanup()方法一定会被调用，因为Supervisor可以对集群的工作进程使用kill -9命令强制杀死进程命令。
     * <p>
     * 如果在本地模式下运行Storm，当拓扑被杀死的时候，可以保证cleanup()方法一定会被调用。
     *
     * @param input
     */
    @Override
    public void execute(Tuple input) {
        Long productId = input.getLongByField("productId");
        Long count = productCountMap.get(productId);
        if (count == null) {
            count = 0L;
        }
        count++;
        productCountMap.put(productId, count);
        log.info("log: productId = {}, count = {}", productId, count);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    private class ProductCountThread implements Runnable {
        public void run() {
            List<Map.Entry<Long, Long>> topnProductList = new ArrayList<>();
            while (true) {
                try {
                    topnProductList.clear();
                    int topn = 3;
                    if (productCountMap.size() == 0) {
                        Utils.sleep(1000);
                        continue;
                    }
                    for (Map.Entry<Long, Long> productCountEntry : productCountMap.entrySet()) {
                        if (topnProductList.size() == 0) {
                            topnProductList.add(productCountEntry);
                        } else {
                            // 比较大小，生成最热topn的算法有很多种
                            // 但是我这里为了简化起见，不想引入过多的数据结构和算法的的东西
                            // 很有可能还是会有漏洞，但是我已经反复推演了一下了，而且也画图分析过这个算法的运行流程了
                            boolean bigger = false;

                            for (int i = 0; i < topnProductList.size(); i++) {
                                Map.Entry<Long, Long> topnProductCountEntry = topnProductList.get(i);

                                if (productCountEntry.getValue() > topnProductCountEntry.getValue()) {
                                    int lastIndex = topnProductList.size() < topn ? topnProductList.size() - 1 : topn - 2;
                                    for (int j = lastIndex; j >= i; j--) {
                                        if (j + 1 == topnProductList.size()) {
                                            topnProductList.add(null);
                                        }
                                        topnProductList.set(j + 1, topnProductList.get(j));
                                    }
                                    topnProductList.set(i, productCountEntry);
                                    bigger = true;
                                    break;
                                }
                            }
                            if (!bigger) {
                                if (topnProductList.size() < topn) {
                                    topnProductList.add(productCountEntry);
                                }
                            }
                        }
                    }

                    // 获取到topN后，需要写入zookeeper的taskid对应的节点
                    String topnProductListJson = JSONArray.toJSONString(topnProductList);

                    String path = ZkConst.HOT_PRODUCT_PREFIX + taskid;
                    zkSession.setDataNode(path, topnProductListJson);

                    // 每次更新完zk上的topnProductListJson，就将/taskid-status-data-[taskid]重置为false
                    log.info("Update hot product list, path = {} status = false", ZkConst.TASK_ID_STATUS_LOCK_PREFIX + taskid);
                    zkSession.lock(ZkConst.TASK_ID_STATUS_LOCK_PREFIX + taskid);
                    zkSession.setDataNode(ZkConst.TASK_ID_STATUS_DATA_PREFIX + taskid, "false");
                    zkSession.unlock(ZkConst.TASK_ID_STATUS_LOCK_PREFIX + taskid);

                    // 每分钟统计一次topN
                    Utils.sleep(60000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
