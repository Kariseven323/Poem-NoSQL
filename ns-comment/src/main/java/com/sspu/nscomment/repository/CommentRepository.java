package com.sspu.nscomment.repository;

import com.sspu.nscomment.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {
    @Query("{'poemId': :#{#poemId}}")
    Optional<Comment> findByPoemId(@Param("poemId") String poemId);

    boolean existsByPoemId(String poemId);
}
