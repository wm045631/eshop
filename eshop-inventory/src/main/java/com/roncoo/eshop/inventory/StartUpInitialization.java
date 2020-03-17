package com.roncoo.eshop.inventory;

import com.roncoo.eshop.inventory.thread.RequestProcessorThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartUpInitialization implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("StartUpInitialization:===== 初始化系统监听器 =====");
        log.info("StartUpInitialization:===== 初始化线程池 + 内存队列 =====");
        RequestProcessorThreadPool.init();
    }
}
