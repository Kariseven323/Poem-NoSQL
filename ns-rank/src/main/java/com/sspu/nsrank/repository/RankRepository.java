package com.sspu.nsrank.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
public class RankRepository {

    @Autowired
    private ZSetOperations<String, String> zSetOperations;

    private static final String RANK_KEY = "poetry:rank";

    // 添加或更新诗词排名
    public void updateRank(String poemId, double score) {
        zSetOperations.add(RANK_KEY, poemId, score);
    }

    // 获取前100的排名
    public Set<ZSetOperations.TypedTuple<String>> getTopRanked(int limit) {
        return zSetOperations.reverseRangeWithScores(RANK_KEY, 0, limit - 1);
    }

    // 删除诗词的排名
    public void removeRank(String poemId) {
        zSetOperations.remove(RANK_KEY, poemId);
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    // 批量更新排名
    public void updateRankBatch(Map<String, Double> scores) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            BoundZSetOperations<String, String> zSetOps = redisTemplate.boundZSetOps(RANK_KEY);

            scores.forEach((poemId, score) -> {
                Double currentScore = zSetOps.score(poemId);
                if (currentScore == null || !currentScore.equals(score)) {
                    zSetOps.add(poemId, score);
                }
            });

            return null;
        });
    }
}
