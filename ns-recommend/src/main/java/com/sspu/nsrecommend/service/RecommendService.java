package com.sspu.nsrecommend.service;

import com.sspu.nsrecommend.entity.UserBehavior;
import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nsrecommend.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    // 基于用户行为数据推荐诗词
    public List<AncientPoetry> recommendPoems(String userId) {
        Optional<UserBehavior> optionalUserBehavior = userBehaviorRepository.findByUserId(userId);

        if (optionalUserBehavior.isEmpty()) {
            return ancientPoetryRepository.findAll().stream().limit(10).collect(Collectors.toList()); // 默认推荐
        }

        UserBehavior userBehavior = optionalUserBehavior.get();
        return userBehavior.getBehaviors().stream()
                .sorted(Comparator.comparingInt(UserBehavior.Behavior::getViewCount).reversed())
                .flatMap(behavior -> ancientPoetryRepository.findByType(behavior.getType()).stream())
                .limit(10) // 限制返回结果数
                .collect(Collectors.toList());
    }

    // 协同过滤推荐（基于用户行为相似度）
    public List<AncientPoetry> collaborativeFilteringRecommend(String userId) {
        // 获取所有用户行为数据
        List<UserBehavior> allUserBehaviors = userBehaviorRepository.findAll();

        // 当前用户行为数据
        Optional<UserBehavior> optionalUserBehavior = userBehaviorRepository.findByUserId(userId);
        if (optionalUserBehavior.isEmpty()) {
            return ancientPoetryRepository.findAll().stream().limit(10).collect(Collectors.toList()); // 默认推荐
        }
        UserBehavior currentUserBehavior = optionalUserBehavior.get();

        // 计算与其他用户的相似度（简单示例：基于行为类型交集数量）
        Map<String, Integer> similarityMap = new HashMap<>();
        for (UserBehavior userBehavior : allUserBehaviors) {
            if (userBehavior.getUserId().equals(userId)) continue; // 跳过自己
            int similarity = calculateSimilarity(currentUserBehavior, userBehavior);
            similarityMap.put(userBehavior.getUserId(), similarity);
        }

        // 找到相似度最高的用户
        String mostSimilarUserId = similarityMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostSimilarUserId == null) {
            return ancientPoetryRepository.findAll().stream().limit(10).collect(Collectors.toList());
        }

        // 获取相似用户的行为数据并推荐诗词
        UserBehavior mostSimilarUserBehavior = userBehaviorRepository.findByUserId(mostSimilarUserId).orElse(null);
        if (mostSimilarUserBehavior == null) {
            return ancientPoetryRepository.findAll().stream().limit(10).collect(Collectors.toList());
        }

        return mostSimilarUserBehavior.getBehaviors().stream()
                .flatMap(behavior -> ancientPoetryRepository.findByType(behavior.getType()).stream())
                .limit(10)
                .collect(Collectors.toList());
    }

    // 简单的相似度计算逻辑（可以改进）
    private int calculateSimilarity(UserBehavior user1, UserBehavior user2) {
        Set<String> types1 = user1.getBehaviors().stream().map(UserBehavior.Behavior::getType).collect(Collectors.toSet());
        Set<String> types2 = user2.getBehaviors().stream().map(UserBehavior.Behavior::getType).collect(Collectors.toSet());
        types1.retainAll(types2); // 交集
        return types1.size();
    }
}
