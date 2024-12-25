package com.sspu.nslike.initializer;

import com.sspu.nslike.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitializeRunner implements CommandLineRunner {

    @Autowired
    private PoemService poemService;

    @Override
    public void run(String... args) {
        System.out.println("异步初始化 MongoDB 中的 PoemLike 数据...");
        poemService.initializeLikesAsync(); // 调用异步方法
    }
}
