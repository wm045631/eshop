package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

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
                request.process();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
