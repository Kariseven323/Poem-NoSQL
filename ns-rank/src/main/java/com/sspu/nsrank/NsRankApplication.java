package com.sspu.nsrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.sspu.nsrank", "com.sspu.nslike"})
@EnableScheduling // 开启定时任务支持
@EnableDiscoveryClient
public class NsRankApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsRankApplication.class, args);
    }
}
