package com.roncoo.eshop.cache;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.config.RedissonConfig;
import com.roncoo.eshop.cache.config.ZookeeperConfig;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {ZookeeperConfig.class, ZookeeperSession.class, RedissonConfig.class}, initializers = ConfigFileApplicationContextInitializer.class)
//开启自动配置，排除springjdbc自动配置
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Slf4j
@TestPropertySource(value = "classpath:application.yaml")
@EnableConfigurationProperties
public class ZkTest {

    @Autowired
    ZookeeperSession zookeeperSession;

    @Test
    public void testGet() {
        String path = "/task-hot-product-list-5";
        String dataNode = zookeeperSession.getDataNode(path);
        System.out.println(dataNode);
        JSONArray productidArray = JSONArray.parseArray(dataNode);
        JSONObject o = (JSONObject) productidArray.get(0);
        System.out.println(o.getLong("key"));

        System.out.println(o.getLong("value"));
    }
}
