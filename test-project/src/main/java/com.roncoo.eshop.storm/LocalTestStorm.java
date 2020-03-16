package com.roncoo.eshop.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class LocalTestStorm {
    public static void main(String[] args) {
        //第一步，定义TopologyBuilder对象，用于构建拓扑
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        //第二步，设置spout和bolt
        topologyBuilder.setSpout("RandomSentenceSpout", new RandomSentenceSpout(), 5);

        topologyBuilder.setBolt("SplitSentenceBolt", new SplitSentenceBolt(),10)
                .shuffleGrouping("RandomSentenceSpout");

        topologyBuilder.setBolt("WordCountBolt", new WordCountBolt(),5)
                .fieldsGrouping("SplitSentenceBolt",new Fields("word"));

        topologyBuilder.setBolt("PrintBolt", new PrintBolt(),1)
                .shuffleGrouping("WordCountBolt");

        //第三步，构建Topology对象
        StormTopology topology = topologyBuilder.createTopology();

        //第四步，提交拓扑到集群，这里先提交到本地的模拟环境中进行测试
        LocalCluster localCluster = new LocalCluster();
        Config config = new Config();
        localCluster.submitTopology("WordCountTopology", config, topology);
    }
}
