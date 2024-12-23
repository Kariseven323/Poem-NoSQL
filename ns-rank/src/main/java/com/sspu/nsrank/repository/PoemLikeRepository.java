package com.sspu.nsrank.repository;

import com.sspu.nslike.model.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
}
