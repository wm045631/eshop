package com.roncoo.eshop.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

public class SplitSentenceBolt extends BaseRichBolt {

    private OutputCollector outputCollector;

    /**
     * @param map                     配置文件
     * @param topologyContext
     * @param outputCollector       Bolt的tuple发射器
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.outputCollector = outputCollector;
    }

    /**
     * 处理接收到的每个tuple
     *
     * @param tuple
     */
    @Override
    public void execute(Tuple tuple) {
        // spout定义的Field
        String sentence = tuple.getStringByField("sentence");
        String[] words = sentence.trim().split(" ");
        for (String word : words) {
            // 将每个word发送出去
            System.out.println("send word :" + word);
            outputCollector.emit(new Values(word));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }
}
