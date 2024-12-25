package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 诗词点赞实体类
 * 用于存储诗词的点赞信息
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Data
@Document(collection = "poem_likes")
public class PoemLike {
    /**
     * 诗词ID
     */
    @Id
    private String id;

    /**
     * 诗词ID
     */
    private String poemId;

    /**
     * 点赞数
     */
    private int likeCount = 0;

    /**
     * 访问量
     */
    private int visitCount = 0;

    /**
     * 点赞用户ID集合
     */
    private Set<String> likedUserIds = new HashSet<>();

    /**
     * 热度分数
     */
    private double hotScore = 0.0;

    /**
     * 初始化方法
     */
    public void initialize() {
        if (likedUserIds == null) {
            likedUserIds = new HashSet<>();
        }
        if (likeCount < 0) {
            likeCount = 0;
        }
        if (visitCount < 0) {
            visitCount = 0;
        }
    }

    /**
     * 计算热度分数
     */
    public void calculateHotScore() {
        this.hotScore = this.likeCount * 0.7 + this.visitCount * 0.3;
    }

    /**
     * 增加访问量
     */
    public void incrementVisitCount() {
        this.visitCount++;
        calculateHotScore();
    }

    /**
     * 添加点赞
     * @param userId 用户ID
     */
    public void addLike(String userId) {
        if (likedUserIds == null) {
            likedUserIds = new HashSet<>();
        }
        if (likedUserIds.add(userId)) {
            likeCount++;
            calculateHotScore();
        }
    }

    /**
     * 移除点赞
     * @param userId 用户ID
     */
    public void removeLike(String userId) {
        if (likedUserIds != null && likedUserIds.remove(userId)) {
            likeCount = Math.max(0, likeCount - 1);
            calculateHotScore();
        }
    }
}
