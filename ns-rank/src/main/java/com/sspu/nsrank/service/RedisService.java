package com.sspu.nsrank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 从Redis中获取诗词排行数据
    public Map<Object, Object> getTopPoemsFromRedis() {
        return redisTemplate.opsForHash().entries("top_poems");
    }
}
