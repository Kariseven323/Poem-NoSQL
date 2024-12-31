package com.sspu.nscomment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 核心线程数
        executor.setMaxPoolSize(20);  // 最大线程数
        executor.setQueueCapacity(500); // 队列容量
        executor.setThreadNamePrefix("AsyncExecutor-"); // 线程名称前缀
        executor.setKeepAliveSeconds(60); // 空闲线程的存活时间（秒）
        executor.initialize();
        return executor;
    }
}
