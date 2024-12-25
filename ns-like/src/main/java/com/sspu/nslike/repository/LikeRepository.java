package com.sspu.nslike.repository;

import com.sspu.nslike.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 根据诗词ID和父评论ID为空的评论列表
     * @param poemId 诗词ID
     * @return 返回评论列表
     */
    List<Comment> findByPoemIdAndParentIdIsNull(String poemId);

    /**
     * 判断诗词ID和父评论ID为空的评论是否存在
     * @param poemId 诗词ID
     * @return 返回是否存在
     */
    boolean existsByPoemIdAndParentIdIsNull(String poemId);
}