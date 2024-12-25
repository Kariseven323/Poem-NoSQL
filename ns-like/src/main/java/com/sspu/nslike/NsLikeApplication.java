package com.sspu.nslike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class NsLikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsLikeApplication.class, args);
        System.out.println("Ns-Like 模块已启动！");
    }
}
