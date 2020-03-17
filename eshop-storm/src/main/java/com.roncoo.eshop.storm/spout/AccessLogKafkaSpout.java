package com.roncoo.eshop.storm.spout;

import com.roncoo.eshop.storm.kafka.KafkaMessageProcessor;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class AccessLogKafkaSpout extends BaseRichSpout {

    private static final Logger log = LoggerFactory.getLogger(AccessLogKafkaSpout.class);

    private SpoutOutputCollector collector;

    private ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10000);

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        new Thread(new KafkaMessageProcessor(queue)).start();
    }

    @Override
    public void nextTuple() {
        if (queue.size() > 0) {
            try {
                String msg = queue.take();
                collector.emit(new Values(msg));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Utils.sleep(100);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }
}
