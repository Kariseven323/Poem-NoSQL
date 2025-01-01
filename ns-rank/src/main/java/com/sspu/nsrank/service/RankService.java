package com.sspu.nsrank.service;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.entity.PoemLike;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RANK_CACHE_KEY = "rank:top100";

    // 从 Redis 获取前 100 的诗词
    public List<AncientPoetry> getTop100Poems() {
        return (List<AncientPoetry>) redisTemplate.opsForValue().get(RANK_CACHE_KEY);
    }

    // 定时任务：每分钟更新榜单
    @Scheduled(cron = "0 * * * * ?")
    public void updateTop100Rank() {
        // 从 MongoDB 获取点赞量前 100 的诗词 ID
        List<String> topPoemIds = poemLikeRepository.findTop100ByOrderByLikeCountDesc()
                .stream()
                .map(PoemLike::getPoemId)
                .collect(Collectors.toList());

        // 从 MySQL 中获取对应的诗词信息
        List<AncientPoetry> topPoems = ancientPoetryRepository.findAllById(topPoemIds);

        // 缓存到 Redis
        redisTemplate.opsForValue().set(RANK_CACHE_KEY, topPoems);
    }
}
