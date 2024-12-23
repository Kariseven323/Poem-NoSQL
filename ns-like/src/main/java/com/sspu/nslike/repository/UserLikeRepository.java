package com.sspu.nslike.repository;

import com.sspu.nslike.model.UserLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户点赞数据访问接口
 * 提供用户点赞相关的数据操作方法
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Repository
public interface UserLikeRepository extends MongoRepository<UserLike, String> {
    /**
     * 根据用户ID查找点赞记录
     * @param userId 用户ID
     * @return 返回用户点赞记录
     */
    Optional<UserLike> findByUserId(String userId);
}
