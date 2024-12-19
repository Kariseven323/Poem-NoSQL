package com.sspu.nsrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoRepositories(basePackages = "com.sspu.nslike.repository") // 指定 Repository 包路径
public class NsRankApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsRankApplication.class, args);
        System.out.println("Ns-Rank 模块启动成功！");
    }
}
