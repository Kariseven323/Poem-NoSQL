package com.sspu.nssearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sspu.nssearch.entity.AncientPoetry;
import com.sspu.nssearch.repository.elasticsearch.AncientPoetrySearchRepository;
import com.sspu.nssearch.repository.jpa.PoetryJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class SearchService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PoetryJpaRepository poetryJpaRepository;

    @Autowired
    private AncientPoetrySearchRepository poetrySearchRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Async
    public CompletableFuture<List<AncientPoetry>> findByElasticsearch(String keyword) {
        return CompletableFuture.completedFuture(poetrySearchRepository.findByTitleContaining(keyword));
    }

    public List<AncientPoetry> search(String keyword, int page, int size) {
        String cacheKey = "search:" + keyword + ":page:" + page + ":size:" + size;

        // 检查缓存
        Object cachedObject = redisTemplate.opsForValue().get(cacheKey);
        if (cachedObject != null) {
            try {
                return objectMapper.convertValue(cachedObject, new TypeReference<List<AncientPoetry>>() {});
            } catch (Exception e) {
                // 清理不合法的缓存数据
                System.err.println("Redis 缓存反序列化失败，清理缓存：" + cacheKey);
                redisTemplate.delete(cacheKey);
            }
        }

        // 分页查询
        Pageable pageable = PageRequest.of(page, size);
        CompletableFuture<List<AncientPoetry>> elasticsearchFuture = findByElasticsearch(keyword);
        List<AncientPoetry> resultsByWriter = poetryJpaRepository.findByWriterContaining(keyword, pageable);
        List<AncientPoetry> resultsByContent = poetryJpaRepository.findByContentContaining(keyword, pageable);

        // 等待异步任务完成
        CompletableFuture.allOf(elasticsearchFuture).join();

        try {
            List<AncientPoetry> results = new ArrayList<>();
            results.addAll(elasticsearchFuture.get());
            results.addAll(resultsByWriter);
            results.addAll(resultsByContent);
            results = results.stream().distinct().toList();

            // 写入缓存
            redisTemplate.opsForValue().set(cacheKey, results, 10, TimeUnit.MINUTES);
            return results;
        } catch (Exception e) {
            throw new RuntimeException("异步查询失败", e);
        }
    }
}
