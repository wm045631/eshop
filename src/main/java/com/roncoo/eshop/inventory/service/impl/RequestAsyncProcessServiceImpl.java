package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.config.InventoryConfig;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import com.roncoo.eshop.inventory.thread.RequestProcessorThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

    @Autowired
    private InventoryConfig inventoryConfig;

    @Override
    public void process(Request request) {
        // 做请求的路由，根据每个商品的id，路由到对应的内存队列中去
    }

    private ArrayBlockingQueue<Request> getRoutingQueue(Long productId) {
        RequestProcessorThreadPool instance = RequestProcessorThreadPool.getInstance();
        List<ArrayBlockingQueue> queues = instance.getQueues();
        String key = String.valueOf(productId);
        int h;
        // hashCode值的高位与低位做异或运算
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        // 对hash值进行取模，然后路由
        int index = (queues.size() - 1) & hash;
        return queues.get(index);
    }
}
