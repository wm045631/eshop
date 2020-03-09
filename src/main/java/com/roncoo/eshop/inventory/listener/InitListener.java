package com.roncoo.eshop.inventory.listener;

import com.roncoo.eshop.inventory.thread.RequestProcessorThreadPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化监听器
 */
public class InitListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("===== 初始化系统监听器 ==========");
        System.out.println("===== 初始化线程池 + 内存队列 ==========");
        RequestProcessorThreadPool.init();
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
