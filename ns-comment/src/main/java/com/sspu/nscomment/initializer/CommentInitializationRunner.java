package com.sspu.nscomment.initializer;

import com.sspu.nscomment.service.CommentInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommentInitializationRunner implements CommandLineRunner {

    @Autowired
    private CommentInitializationService commentInitializationService;

    @Override
    public void run(String... args) {
        System.out.println("正在初始化 MongoDB 中的评论结构...");
        commentInitializationService.initializeComments();
        System.out.println("评论结构初始化完成！");
    }
}
