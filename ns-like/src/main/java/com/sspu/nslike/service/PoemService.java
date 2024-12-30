package com.sspu.nslike.service;

import com.sspu.nslike.entity.PoemLike;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PoemService {

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "poem:likes:";

    // 初始化点赞数据（同步到 MongoDB 和 Redis）
    @Async
    public void initializeLikesAsync() {
        ancientPoetryRepository.findAll().forEach(poetry -> {
            // 检查 MongoDB 是否存在该诗词
            if (!poemLikeRepository.existsById(poetry.getId())) {
                // 如果不存在，初始化 PoemLike 数据
                PoemLike newLike = new PoemLike();
                newLike.setId(poetry.getId());
                newLike.setPoemId(poetry.getId());
                poemLikeRepository.save(newLike);

                // 同步到 Redis
                String cacheKey = CACHE_PREFIX + poetry.getId();
                redisTemplate.opsForValue().set(cacheKey, newLike, 10, TimeUnit.MINUTES);
            } else {
                // 如果 MongoDB 已存在，但 Redis 不存在，则同步到 Redis
                String cacheKey = CACHE_PREFIX + poetry.getId();
                if (!Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                    PoemLike existingLike = poemLikeRepository.findById(poetry.getId()).orElse(null);
                    if (existingLike != null) {
                        redisTemplate.opsForValue().set(cacheKey, existingLike, 10, TimeUnit.MINUTES);
                    }
                }
            }
        });
        System.out.println("PoemLike 数据初始化完成并同步到 Redis！");
    }

    // 点赞/取消点赞
    public boolean toggleLike(String poemId, String userId) {
        String cacheKey = CACHE_PREFIX + poemId;
        PoemLike poemLike;

        // 1. 先从 Redis 中获取数据
        poemLike = (PoemLike) redisTemplate.opsForValue().get(cacheKey);

        if (poemLike == null) {
            // 2. 如果 Redis 中没有，查询 MongoDB 并缓存
            Optional<PoemLike> optionalPoemLike = poemLikeRepository.findByPoemId(poemId);
            if (optionalPoemLike.isEmpty()) {
                return false; // 诗词不存在
            }
            poemLike = optionalPoemLike.get();
            redisTemplate.opsForValue().set(cacheKey, poemLike, 10, TimeUnit.MINUTES); // 缓存10分钟
        }

        // 3. 处理点赞/取消点赞逻辑
        if (poemLike.getUserIds().contains(userId)) {
            // 用户已点赞，取消点赞
            poemLike.getUserIds().remove(userId);
            poemLike.setLikeCount(poemLike.getLikeCount() - 1);
        } else {
            // 用户未点赞，添加点赞
            poemLike.getUserIds().add(userId);
            poemLike.setLikeCount(poemLike.getLikeCount() + 1);
        }

        // 4. 更新 Redis 缓存
        redisTemplate.opsForValue().set(cacheKey, poemLike, 10, TimeUnit.MINUTES);

        // 5. 异步更新到 MongoDB
        saveToDatabaseAsync(poemLike);

        return true;
    }

    // 异步保存数据到 MongoDB
    @Async
    public void saveToDatabaseAsync(PoemLike poemLike) {
        poemLikeRepository.save(poemLike);
    }
}
