package com.sspu.nslike.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisLikeConfig {

    @Bean("likeRedisConfig")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置 key 的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 设置 value 的序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
