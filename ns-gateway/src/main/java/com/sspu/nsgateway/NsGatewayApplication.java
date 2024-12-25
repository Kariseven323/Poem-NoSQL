package com.sspu.nsgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 启用 Eureka 客户端
public class NsGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsGatewayApplication.class, args);
        System.out.println("Ns-Gateway 模块成功启动！");
    }
}
