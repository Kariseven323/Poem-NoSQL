package com.sspu.nsrecommend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.sspu.nslike.repository")
@EntityScan(basePackages = "com.sspu.nslike.entity")
public class NsRecommendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsRecommendApplication.class, args);
        System.out.println("Ns-Recommend 模块已启动！");
    }
}
