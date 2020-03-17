package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.InventoryCntCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.InventoryCntDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

@Slf4j
public class WorkThread implements Callable<Boolean> {

    private ArrayBlockingQueue<Request> queue;

    public WorkThread(ArrayBlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public Boolean call() {
        // 循环从queue中获取请求进行处理
        try {
            while (true) {
                Request request = queue.take();

                if (!request.isForceRefresh()) {  // 如果不是强制刷新，才进行读请求去重
                    // 1、先做读请求的去重
                    RequestQueue requestQueue = RequestQueue.getInstance();
                    Map<Long, Boolean> flagMap = requestQueue.getFlagMap();

                    // 如果是写请求 ==> 将标志位置为：true
                    if (request instanceof InventoryCntDBUpdateRequest) {
                        log.info("=======写请求=======");
                        flagMap.put(request.getProductId(), true);
                    } else if (request instanceof InventoryCntCacheRefreshRequest) {
                        log.info("=======读请求=======");
                        Boolean flag = flagMap.get(request.getProductId());

                        // flag为null，表示之前没有写请求，数据库中没有该条记录。【通过别的程序导入的除外】
                        if (flag == null) {
                            // 设为false，后面的数据库中没有数据读请求也会被过滤掉，不写入内存队列
                            flagMap.put(request.getProductId(), false);
                        }

                        // 如果是读请求，并且标志位为：false，说明前面一个是写请求 + 一个读请求 ==> 直接return，controller层会循环查询redis的
                        if (flag != null && !flag) {
                            return true;
                        }

                        // 如果是读请求，并且标志位为：true，说明前面一个是写请求 ==> 将标志位置为：false
                        if (flag != null && flag) {
                            flagMap.put(request.getProductId(), false);
                        }
                    }
                }
                request.process();
                // 读请求执行完后，标志位一直是false，一旦redis的该数据被LRU了，后面的读请求都不会走request.process()了【无法查询数据更新缓存】
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
