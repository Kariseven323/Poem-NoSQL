package com.sspu.nuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NsUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(NsUserApplication.class, args);
        System.out.println("Ns-User Application is running...");
    }
}
