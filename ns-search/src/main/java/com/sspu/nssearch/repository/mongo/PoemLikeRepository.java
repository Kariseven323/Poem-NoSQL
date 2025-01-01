package com.sspu.nssearch.repository.mongo;

import com.sspu.nslike.entity.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {
    // 查询点赞数据
    PoemLike findByPoemId(String poemId);
}
