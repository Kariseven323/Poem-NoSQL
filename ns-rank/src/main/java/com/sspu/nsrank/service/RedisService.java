package com.sspu.nsrank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 从Redis中获取诗词排行数据
    public Map<Object, Object> getTopPoemsFromRedis() {
        log.info("开始从Redis获取诗词数据");
        try {
            Map<Object, Object> poems = redisTemplate.opsForHash().entries("top_poems");
            log.info("从Redis获取到 {} 首诗词", poems.size());
            return poems;
        } catch (Exception e) {
            log.error("从Redis获取诗词数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("从Redis获取诗词数据失败: " + e.getMessage());
        }
    }
}
