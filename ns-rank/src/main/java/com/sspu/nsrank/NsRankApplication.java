package com.sspu.nsrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 启用服务发现，适用于微服务架构中Eureka或其他注册中心
public class NsRankApplication {

    public static void main(String[] args) {
        SpringApplication.run(NsRankApplication.class, args);
        System.out.println("Ns-Rank 模块成功启动！");
    }
}
