package com.roncoo.eshop.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class PrintBolt extends BaseRichBolt {

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    public void execute(Tuple input) {
        String word = input.getStringByField("word");
        Long count = input.getLongByField("count");

        // 打印上游传递的数据
        System.out.println(word + " : " + count);
        // 注意：这里不需要再向下游传递数据了，因为没有下游了
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}