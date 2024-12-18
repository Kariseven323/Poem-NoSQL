package com.sspu.nslike.repository;

import com.sspu.nslike.model.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
    // 根据诗词ID查找点赞记录
    Optional<PoemLike> findByPoemId(String poemId);
}