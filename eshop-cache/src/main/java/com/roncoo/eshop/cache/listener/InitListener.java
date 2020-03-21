package com.roncoo.eshop.cache.listener;

import com.roncoo.eshop.cache.prewarm.CachePrewarmThread;
import com.roncoo.eshop.cache.spring.SpringContext;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 系统初始化的监听器
 * @author Administrator
 *
 */
public class InitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        // 初始化ApplicationContext，其他类可以不通过注解获取spring容器里面的对象
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        // 启动缓存预热
        new Thread(new CachePrewarmThread()).start();
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
