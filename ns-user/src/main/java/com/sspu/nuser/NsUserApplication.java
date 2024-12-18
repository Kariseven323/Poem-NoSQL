/**
 * @file NsUserApplication.java
 * @description 用户服务启动类
 * 功能：
 * 1. 启动用户微服务
 * 2. 启用服务发现客户端
 * 3. 配置 Spring Boot 应用
 * 4. 初始化应用上下文
 */

package com.sspu.nuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NsUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsUserApplication.class, args);
        System.out.println("Ns-User Application is running...");
    }
}
