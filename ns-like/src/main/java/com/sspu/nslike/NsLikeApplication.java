package com.sspu.nslike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 启用 Eureka Client
public class NsLikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NsLikeApplication.class, args);
        System.out.println("Ns-Like 模块成功启动并注册到 Eureka!");
    }
}