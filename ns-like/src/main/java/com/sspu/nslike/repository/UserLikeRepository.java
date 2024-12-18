package com.sspu.nslike.repository;

import com.sspu.nslike.model.UserLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserLikeRepository extends MongoRepository<UserLike, String> {
    Optional<UserLike> findByUserIdAndTargetId(String userId, String targetId);
}
