package com.roncoo.eshop.storm.kafka;

import com.roncoo.eshop.storm.spout.AccessLogKafkaSpout;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

public class KafkaMessageProcessor implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageProcessor.class);

    private String kafkaServer;
    private String topic;
    private String groupId;

    private ArrayBlockingQueue<String> queue;

    public KafkaMessageProcessor(ArrayBlockingQueue<String> queue) {
        Yaml yaml = new Yaml();
        InputStream in = AccessLogKafkaSpout.class.getClassLoader().getResourceAsStream("kafka_config.yaml");
        Map<String, String> kafkaConfig = yaml.loadAs(in, Map.class);
        this.groupId = kafkaConfig.get("kafka_consumer_groupid");
        this.kafkaServer = kafkaConfig.get("kafka_server");
        this.topic = kafkaConfig.get("kafka_access_log_topic");
        this.queue = queue;
        log.info("Init kafka config. kafkaServer ={}, groupId = {}, topic = {}", this.kafkaServer, this.groupId, this.topic);
    }

    public Properties defaultConsumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    /**
     * 日志数据的样例
     * {
     *     "method":"GET",
     *     "http_version":1.1,
     *     "raw_reader":"GET /ehcache/getProductInfo?productId=2&shopId=1 HTTP/1.1 Host: 172.20.3.174:70 User-Agent: lua-resty-http/0.14 (Lua) ngx_lua/9014",
     *     "uri_args":{
     *         "productId":"2",
     *         "shopId":"1"
     *     },
     *     "headers":{
     *         "host":"172.20.3.174:70",
     *         "user-agent":"lua-resty-http/0.14 (Lua) ngx_lua/9014"
     *     }
     * }
     */
    @Override
    public void run() {
        Properties props = defaultConsumerConfigs();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(this.topic));
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    log.info("topic:[{}],route_key:{},partition:{},offset:{},message:{}",
                            record.topic(), record.key(), record.partition(), record.offset(), message);
                    queue.put(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
