package com.sspu.nsrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.sspu.nsrank.repository") // 扫描 Repository 包
public class NsRankApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsRankApplication.class, args);
        System.out.println("Ns-Rank 模块已启动！");
    }
}
