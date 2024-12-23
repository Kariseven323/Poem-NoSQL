package com.sspu.nslike.repository;

import com.sspu.nslike.model.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 诗词点赞数据访问接口
 * 提供诗词点赞相关的数据操作方法
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Repository
public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
    /**
     * 根据诗词ID查找点赞记录
     * @param poemId 诗词ID
     * @return 返回诗词点赞记录
     */
    Optional<PoemLike> findByPoemId(String poemId);
}