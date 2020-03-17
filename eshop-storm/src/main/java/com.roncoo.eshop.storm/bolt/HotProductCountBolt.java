package com.roncoo.eshop.storm.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.trident.util.LRUMap;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HotProductCountBolt extends BaseRichBolt {

    private static final Logger log = LoggerFactory.getLogger(HotProductCountBolt.class);

    // 根据内存和数据量计算的容量。采用LRUMap
    private LRUMap<Long, Long> productCountMap = new LRUMap<Long, Long>(1000);

    /**
     * 针对LRUmap进行清理
     *
     * @param stormConf
     * @param context
     * @param collector
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

    }

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
    public void declareOutputFields(OutputFieldsDeclarer declarer) {}
}
