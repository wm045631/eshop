package com.roncoo.eshop.inventory.request;

/**
 * 用来封装request
 * 这些request最终需要放在线程池的内存队列，等待执行
 */
public interface Request {

    /**
     * 封装这个request的处理逻辑
     */
    void process();

    Long getProductId();

    boolean isForceRefresh();
}
