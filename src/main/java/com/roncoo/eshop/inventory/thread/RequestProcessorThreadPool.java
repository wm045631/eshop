package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 系统启动的时候，初始化一个请求处理的线程池：
 * <p>
 * 采用单例模式
 * 采用绝对线程安全的方式 --> 静态内部类
 */
@Data
public class RequestProcessorThreadPool {

    private int nThread = 10;

    private int queueCapacity = 1000;
    // 线程池
    private ExecutorService threadPool = Executors.newFixedThreadPool(nThread);
    // 内存队列
    private List<ArrayBlockingQueue> queues = new ArrayList<>();

    public RequestProcessorThreadPool() {
        // 将每个线程绑定一个队列
        for (int i = 0; i < nThread; i++) {
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(queueCapacity);
            queues.add(queue);
            threadPool.submit(new WorkThread(queue));
        }
    }

    /**
     * 初始化入口
     */
    public static void init() {
        getInstance();
    }

    /**
     * 利用jvm保证多线程并发安全
     *
     * @return
     */
    public static RequestProcessorThreadPool getInstance() {
        return Singleton.getInstance();
    }


    /**
     * 静态内部类只会被初始化一次
     */
    private static class Singleton {
        private static RequestProcessorThreadPool instance;

        static {
            // 执行RequestProcessorThreadPool的空参构造
            instance = new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance() {
            return instance;
        }
    }
}
