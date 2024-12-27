package com.sspu.nscomment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.sspu.nslike.repository") // 指定 JPA 仓库的包
@EntityScan(basePackages = "com.sspu.nslike.entity") // 指定 JPA 实体的包
public class NsCommentApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsCommentApplication.class, args);
        System.out.println("Ns-Comment 模块成功启动！");
    }
}
