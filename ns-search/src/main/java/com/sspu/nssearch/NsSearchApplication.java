package com.sspu.nssearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync // 启用异步功能
@EnableJpaRepositories(basePackages = "com.sspu.nssearch.repository.jpa") // JPA 仓库路径
@EnableMongoRepositories(basePackages = "com.sspu.nssearch.repository.mongo") // MongoDB 仓库路径
@EnableElasticsearchRepositories(basePackages = "com.sspu.nssearch.repository.elasticsearch") // Elasticsearch 仓库路径
@EntityScan(basePackages = "com.sspu.nssearch.entity") // 扫描实体类
public class NsSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsSearchApplication.class, args);
        System.out.println("Ns-Search 模块已启动！");
    }
}
