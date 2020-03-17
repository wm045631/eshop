package com.roncoo.eshop.inventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 */
public class RequestQueue {

    /**
     * 内存队列
     */
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 标识位map,必须承载线程安全。
     * 有三种值：
     * null： 前面没有该id对应的请求
     * true： 前面有个一个写请求
     * false：前面有一个写请求 +  一个读请求
     */
    private Map<Long, Boolean> flagMap = new ConcurrentHashMap();

    /**
     * 单例有很多种方式去实现：我采取绝对线程安全的一种方式
     * <p>
     * 静态内部类的方式，去初始化单例
     */
    private static class Singleton {

        private static RequestQueue instance;

        static {
            instance = new RequestQueue();
        }

        public static RequestQueue getInstance() {
            return instance;
        }

    }

    /**
     * jvm的机制去保证多线程并发安全
     * <p>
     * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
     *
     * @return
     */
    public static RequestQueue getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 添加一个内存队列
     *
     * @param queue
     */
    public void addQueue(ArrayBlockingQueue<Request> queue) {
        this.queues.add(queue);
    }

    /**
     * 获取内存队列的数量
     *
     * @return
     */
    public int queueSize() {
        return queues.size();
    }

    /**
     * 获取内存队列
     *
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index) {
        return queues.get(index);
    }

    public Map<Long, Boolean> getFlagMap() {
        return flagMap;
    }

}
