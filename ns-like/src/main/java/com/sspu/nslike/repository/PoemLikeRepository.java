package com.sspu.nslike.repository;

import com.sspu.nslike.entity.PoemLike;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
    Optional<PoemLike> findByPoemId(String poemId);
    List<PoemLike> findTop100ByOrderByLikeCountDesc();
    default List<PoemLike> findWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return findAll(pageable).getContent();
    }
}
