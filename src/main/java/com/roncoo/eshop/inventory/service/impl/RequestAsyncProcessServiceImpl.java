package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.config.InventoryConfig;
import com.roncoo.eshop.inventory.request.InventoryCntCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.InventoryCntDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求的异步处理
 */
@Service
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

    @Autowired
    private InventoryConfig inventoryConfig;

    @Override
    public void wrapper(Request request) {
        try {
            // 做请求的路由，根据每个商品的id，路由到对应的内存队列中去
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
            // 将请求放入对应的队列中，完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据productId获取对应的内存队列
     *
     * @param productId
     * @return
     */
    private ArrayBlockingQueue<Request> getRoutingQueue(Long productId) {
        RequestQueue queues = RequestQueue.getInstance();
        String key = String.valueOf(productId);
        int h;
        // hashCode值的高位与低位做异或运算
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        // 对hash值进行取模，然后路由
        int index = (queues.queueSize() - 1) & hash;
        return queues.getQueue(index);
    }
}
