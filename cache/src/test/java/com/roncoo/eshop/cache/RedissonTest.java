package com.roncoo.eshop.cache;

import com.roncoo.eshop.cache.config.RedissonConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {RedissonConfig.class})
//开启自动配置，排除springjdbc自动配置
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class RedissonTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("redisson-test", "hello word");
        System.out.println(valueOperations.get("redisson-test"));

        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        valueOperations.set("redisson-test-string", "hello word wangming");
        System.out.println(valueOperations.get("redisson-test-string"));
    }

    @Test
    public void testDate() throws InterruptedException {
        Date start = new Date();
        Thread.sleep(2000);
        Date end = new Date();
        Date endnull = null;
        System.out.println(start.before(end));
        System.out.println(start.before(endnull));
    }
}
