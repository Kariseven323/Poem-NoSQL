package com.sspu.nsrecommend.service;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nsrecommend.entity.UserBehavior;
import com.sspu.nsrecommend.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorRepository userBehaviorRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    public void recordBehavior(String userId, String poemId) {
        // 从 MySQL 查询诗词
        Optional<AncientPoetry> optionalPoetry = ancientPoetryRepository.findById(poemId);
        if (optionalPoetry.isEmpty()) {
            throw new IllegalArgumentException("诗词不存在，无法记录行为！");
        }

        // 获取诗词类型，并以逗号分隔
        String type = optionalPoetry.get().getType();
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("诗词类型为空，无法记录行为！");
        }
        String[] types = type.split(","); // 按逗号分隔类型

        // 查询用户行为数据
        UserBehavior userBehavior = userBehaviorRepository.findByUserId(userId)
                .orElse(new UserBehavior());
        userBehavior.setUserId(userId);

        // 初始化 behaviors 列表
        if (userBehavior.getBehaviors() == null) {
            userBehavior.setBehaviors(new ArrayList<>());
        }

        // 遍历类型数组，逐个记录到用户行为中
        for (String t : types) {
            String trimmedType = t.trim(); // 去除多余空格
            if (trimmedType.isEmpty()) continue; // 跳过空类型

            // 检查行为是否已存在
            boolean typeExists = false;
            for (UserBehavior.Behavior behavior : userBehavior.getBehaviors()) {
                if (behavior.getType().equals(trimmedType)) {
                    behavior.setViewCount(behavior.getViewCount() + 1); // 增加浏览次数
                    typeExists = true;
                    break;
                }
            }

            // 如果行为不存在，则添加新行为
            if (!typeExists) {
                UserBehavior.Behavior newBehavior = new UserBehavior.Behavior();
                newBehavior.setType(trimmedType);
                newBehavior.setViewCount(1);
                userBehavior.getBehaviors().add(newBehavior);
            }
        }

        // 保存到 MongoDB
        userBehaviorRepository.save(userBehavior);
    }
}
