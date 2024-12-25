package com.sspu.nsrank.repository;

import com.sspu.nslike.model.PoemLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PoemLikeRepository extends MongoRepository<PoemLike, String> {

    // 自定义查询：按点赞数和访问数之和降序排序，限制结果为前50条
    @Query(value = "{}", sort = "{'likeCount': -1, 'visitCount': -1}")
    List<PoemLike> findTop50ByOrderByLikeCountAndVisitCount();
}
