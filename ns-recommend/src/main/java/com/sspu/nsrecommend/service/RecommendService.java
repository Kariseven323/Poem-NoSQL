package com.sspu.nsrecommend.service;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nsrecommend.entity.UserBehavior;
import com.sspu.nsrecommend.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RECOMMEND_CACHE_PREFIX = "recommend:poem:";
    private static final String COLLABORATIVE_CACHE_PREFIX = "collaborative:poem:";

    // 基于用户行为数据推荐诗词
    public List<AncientPoetry> recommendPoems(String userId) {
        Optional<UserBehavior> optionalUserBehavior = userBehaviorRepository.findByUserId(userId);

        if (optionalUserBehavior.isEmpty()) {
            return ancientPoetryRepository.findAll().stream()
                    .filter(poem -> !isPoemInCache(RECOMMEND_CACHE_PREFIX, userId, poem.getId()))
                    .limit(10)
                    .collect(Collectors.toList()); // 默认推荐
        }

        UserBehavior userBehavior = optionalUserBehavior.get();
        List<AncientPoetry> recommendedPoems = userBehavior.getBehaviors().stream()
                .sorted(Comparator.comparingInt(UserBehavior.Behavior::getViewCount).reversed())
                .flatMap(behavior -> ancientPoetryRepository.findByType(behavior.getType()).stream())
                .filter(poem -> !isPoemInCache(RECOMMEND_CACHE_PREFIX, userId, poem.getId())) // 过滤Redis中已推荐的诗词
                .limit(10) // 限制返回结果数
                .collect(Collectors.toList());

        // 将推荐结果存入Redis
        cacheRecommendedPoems(RECOMMEND_CACHE_PREFIX, userId, recommendedPoems);

        return recommendedPoems;
    }

    // 基于协同过滤推荐诗词
    public List<AncientPoetry> collaborativeFilteringRecommend(String userId) {
        List<UserBehavior> allUserBehaviors = userBehaviorRepository.findAll();

        Optional<UserBehavior> optionalUserBehavior = userBehaviorRepository.findByUserId(userId);
        if (optionalUserBehavior.isEmpty()) {
            return ancientPoetryRepository.findAll().stream()
                    .filter(poem -> !isPoemInCache(COLLABORATIVE_CACHE_PREFIX, userId, poem.getId()))
                    .limit(10)
                    .collect(Collectors.toList()); // 默认推荐
        }

        UserBehavior currentUserBehavior = optionalUserBehavior.get();
        Map<String, Double> similarityMap = new HashMap<>();

        for (UserBehavior otherUser : allUserBehaviors) {
            if (!otherUser.getUserId().equals(userId)) {
                double similarity = calculateCosineSimilarity(currentUserBehavior, otherUser);
                similarityMap.put(otherUser.getUserId(), similarity);
            }
        }

        String mostSimilarUserId = similarityMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostSimilarUserId == null) {
            return ancientPoetryRepository.findAll().stream()
                    .filter(poem -> !isPoemInCache(COLLABORATIVE_CACHE_PREFIX, userId, poem.getId()))
                    .limit(10)
                    .collect(Collectors.toList()); // 默认推荐
        }

        UserBehavior mostSimilarUserBehavior = userBehaviorRepository.findByUserId(mostSimilarUserId).orElse(null);
        if (mostSimilarUserBehavior == null) {
            return ancientPoetryRepository.findAll().stream()
                    .filter(poem -> !isPoemInCache(COLLABORATIVE_CACHE_PREFIX, userId, poem.getId()))
                    .limit(10)
                    .collect(Collectors.toList()); // 默认推荐
        }

        List<AncientPoetry> recommendedPoems = mostSimilarUserBehavior.getBehaviors().stream()
                .flatMap(behavior -> ancientPoetryRepository.findByType(behavior.getType()).stream())
                .filter(poem -> !isPoemInCache(COLLABORATIVE_CACHE_PREFIX, userId, poem.getId())) // 过滤Redis中已推荐的诗词
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        // 将推荐结果存入Redis
        cacheRecommendedPoems(COLLABORATIVE_CACHE_PREFIX, userId, recommendedPoems);

        return recommendedPoems;
    }

    // 余弦相似度计算方法
    private double calculateCosineSimilarity(UserBehavior user1, UserBehavior user2) {
        Map<String, Integer> typeCounts1 = user1.getBehaviors().stream()
                .collect(Collectors.toMap(UserBehavior.Behavior::getType, UserBehavior.Behavior::getViewCount));
        Map<String, Integer> typeCounts2 = user2.getBehaviors().stream()
                .collect(Collectors.toMap(UserBehavior.Behavior::getType, UserBehavior.Behavior::getViewCount));

        Set<String> allTypes = new HashSet<>();
        allTypes.addAll(typeCounts1.keySet());
        allTypes.addAll(typeCounts2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String type : allTypes) {
            int count1 = typeCounts1.getOrDefault(type, 0);
            int count2 = typeCounts2.getOrDefault(type, 0);

            dotProduct += count1 * count2;
            norm1 += count1 * count1;
            norm2 += count2 * count2;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2) + 1e-10); // 防止除以0
    }

    // 检查诗词是否在Redis缓存中
    private boolean isPoemInCache(String cachePrefix, String userId, String poemId) {
        String key = cachePrefix + userId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, poemId));
    }

    // 将推荐的诗词ID存入Redis，设置10秒过期
    private void cacheRecommendedPoems(String cachePrefix, String userId, List<AncientPoetry> poems) {
        String key = cachePrefix + userId;
        for (AncientPoetry poem : poems) {
            redisTemplate.opsForSet().add(key, poem.getId());
        }
        redisTemplate.expire(key, 10, TimeUnit.SECONDS); // 设置10秒过期
    }
}
