package com.sspu.nslike.repository;

import com.sspu.nslike.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikeRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPoemIdOrderByLikeCountDesc(String poemId); // 根据点赞数排序
}
