package com.sspu.nscomment.service;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentInitializationService {

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private CommentRepository commentRepository;

    public void initializeComments() {
        // 从 MySQL 中获取所有的诗词
        List<AncientPoetry> allPoems = ancientPoetryRepository.findAll();

        for (AncientPoetry poem : allPoems) {
            String poemId = poem.getId();

            // 检查 MongoDB 中是否存在对应的评论结构
            if (!commentRepository.existsByPoemId(poemId)) {
                // 如果不存在，为该诗创建评论结构
                Comment newComment = new Comment();
                newComment.setPoemId(poemId);
                commentRepository.save(newComment);
                System.out.println("已为诗词 ID " + poemId + " 创建评论结构");
            } else {
                System.out.println("诗词 ID " + poemId + " 的评论结构已存在，跳过");
            }
        }
    }
}
