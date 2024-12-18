/**
 * @file EurekaServerApplication.java
 * @description Eureka 服务注册中心启动类
 * 功能：
 * 1. 启动 Eureka 服务注册中心
 * 2. 提供服务注册和发现功能
 * 3. 管理微服务实例
 * 4. 监控服务健康状态
 */

package com.sspu.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
