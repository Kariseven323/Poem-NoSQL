package com.sspu.nslike.repository;

import com.sspu.nslike.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 点赞数据访问接口
 * 提供基础的点赞数据操作方法
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Repository
public interface LikeRepository extends MongoRepository<Comment, String> {
    /**
     * 根据诗词ID查找评论列表
     * @param poemId 诗词ID
     * @return 返回评论列表
     */
    List<Comment> findByPoemId(String poemId);

    /**
     * 根据用户ID和评论ID查找评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 返回评论
     */
    Optional<Comment> findByUserIdAndId(String userId, String commentId);
}