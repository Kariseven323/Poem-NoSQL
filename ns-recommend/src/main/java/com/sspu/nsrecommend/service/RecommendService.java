package com.sspu.nsrecommend.service;

import com.sspu.nsrecommend.entity.UserBehavior;
import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nsrecommend.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

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
}
