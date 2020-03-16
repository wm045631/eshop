package com.roncoo.eshop.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

public class WordCountTopology {

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();

        /**
         * 第一个参数：spout、bolt组件的名称
         * 第二个参数：指定spout、bolt组件
         * 第三个参数：设置该组件的并行度，这里其实是执行executor的数量。分成多少个task去executor上执行，默认一个executor对应一个task。
         * 通过setNumTasks指定具体的task数目
         */
        builder.setSpout("spout", new RandomSentenceSpout(), 5);

        builder.setBolt("split", new SplitSentenceBolt(), 10)
                .setNumTasks(20)
                .shuffleGrouping("spout");

        builder.setBolt("count", new WordCountBolt(), 12)
                .fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
        conf.setDebug(true);

        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            try {
                StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());
            Utils.sleep(20000);
            cluster.shutdown();
        }
    }
}
