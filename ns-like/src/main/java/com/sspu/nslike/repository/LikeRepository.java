package com.sspu.nslike.repository;

import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.UserLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPoemIdOrderByLikeCountDesc(String poemId); // 根据点赞数排序
}

