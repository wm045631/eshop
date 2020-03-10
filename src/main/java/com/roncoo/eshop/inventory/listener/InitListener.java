package com.roncoo.eshop.inventory.listener;

import com.roncoo.eshop.inventory.thread.RequestProcessorThreadPool;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化监听器
 */
@Slf4j
public class InitListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        log.info("===== 初始化系统监听器 =====\n===== 初始化线程池 + 内存队列 =====");
        // 初始化线程池 + 内存队列
        RequestProcessorThreadPool.init();
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
