package com.sspu.nsrecommend.repository;

import com.sspu.nsrecommend.entity.UserBehavior;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserBehaviorRepository extends MongoRepository<UserBehavior, String> {
    Optional<UserBehavior> findByUserId(String userId);
}
