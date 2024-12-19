package com.sspu.nsrank.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 保存键值对到 Redis 中
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 保存键值对到 Redis 中并设置过期时间
    public void saveWithExpiry(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 从 Redis 中获取值
    public Object find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除 Redis 中的键
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 保存 Hash 类型数据
    public void saveToHash(String hashKey, String field, Object value) {
        redisTemplate.opsForHash().put(hashKey, field, value);
    }

    // 获取 Hash 类型数据的单个字段
    public Object findFromHash(String hashKey, String field) {
        return redisTemplate.opsForHash().get(hashKey, field);
    }

    // 获取 Hash 类型数据的所有字段
    public Map<Object, Object> findAllFromHash(String hashKey) {
        return redisTemplate.opsForHash().entries(hashKey);
    }

    // 删除 Hash 类型中的某个字段
    public void deleteFromHash(String hashKey, String field) {
        redisTemplate.opsForHash().delete(hashKey, field);
    }

    // 保存 List 类型数据
    public void saveToList(String key, List<Object> values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    // 获取 List 类型数据
    public List<Object> findFromList(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // 删除 List 类型数据
    public void deleteFromList(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value); // 删除一个匹配的元素
    }

    // 检查键是否存在
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    // 设置键的过期时间
    public void setExpiry(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    // 获取键的过期时间
    public long getExpiry(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
