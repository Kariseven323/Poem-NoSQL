package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 评论实体类
 * 用于存储诗词评论的相关信息
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Data
@Document(collection = "comments")
public class Comment {
    /**
     * 评论ID
     */
    @Id
    private String id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论者ID
     */
    private String userId;

    /**
     * 诗词ID
     */
    private String poemId;

    /**
     * 点赞数
     */
    private int likeCount = 0;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    /**
     * 父评论ID（用于回复）
     */
    private String parentId;

    // 保存点赞过该评论的用户ID
    private Set<String> likedUserIds = new HashSet<>();

    // 子评论列表
    private List<Comment> replies = new ArrayList<>();

    public void addLike(String userId) {
        if (likedUserIds == null) {
            likedUserIds = new HashSet<>();
        }
        if (likedUserIds.add(userId)) {
            likeCount++;
        }
    }

    public void removeLike(String userId) {
        if (likedUserIds != null && likedUserIds.remove(userId)) {
            likeCount = Math.max(0, likeCount - 1);
        }
    }

    public boolean hasLiked(String userId) {
        return likedUserIds != null && likedUserIds.contains(userId);
    }
}
