package com.roncoo.eshop.inventory.dao.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository("redisDAO")
public class RedisDAOImpl implements RedisDAO {

//    @Resource
//    private JedisCluster jedisCluster;
//
//    @Override
//    public void set(String key, String value) {
//        jedisCluster.set(key, value);
//    }
//
//    @Override
//    public String get(String key) {
//        return jedisCluster.get(key);
//    }
//
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value) {
        ValueOperations<String, String> stringOperations = stringRedisTemplate.opsForValue();
        stringOperations.getAndSet(key, value);
    }

    @Override
    public String get(String key) {
        ValueOperations<String, String> stringOperations = stringRedisTemplate.opsForValue();
        return stringOperations.get(key);
    }

    @Override
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }
}
