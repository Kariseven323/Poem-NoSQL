package com.sspu.nslike.repository;

import com.sspu.nslike.entity.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
    Optional<PoemLike> findByPoemId(String poemId);
    List<PoemLike> findTop100ByOrderByLikeCountDesc();
}
