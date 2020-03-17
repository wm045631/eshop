package com.roncoo.eshop.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;
import java.util.Random;


/**
 * 从数据源获取数据
 */
public class RandomSentenceSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;

    private Random random;

    private String[] sentences = new String[]{
            "the cow jumped over the moon",
            "an apple a day keeps the doctor away",
            "four score and seven years ago",
            "snow white and the seven dwarfs",
            "i am at two with nature"};

    /**
     * 对spout进行初始化的
     * <p>
     * 比如创建一个线程池、数据库连接池、构造一个HTTPClient等等。
     *
     * @param conf
     * @param topologyContext
     * @param spoutOutputCollector:用来发送数据出去的.emit 发射
     */
    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        this.random = new Random();
    }

    /**
     * 这段代码就是在executor的某个task中执行，无限循环nextTuple方法，发射最新的数据出去，形成数据流
     */
    @Override
    public void nextTuple() {
        Utils.sleep(1000);
        String sentence = sentences[random.nextInt(sentences.length)];
        // Values 构建一个tuple
        System.out.println("send sentence :" + sentence);
        collector.emit(new Values(sentence));
    }

    /**
     * 定义collector.emit出去每个tuple中的每个field名称是什么
     *
     * @param outputFieldsDeclarer
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("sentence"));
    }
}