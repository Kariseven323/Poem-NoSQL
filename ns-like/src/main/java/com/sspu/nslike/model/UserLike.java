package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;

/**
 * 用户点赞实体类
 * 用于存储用户的点赞记录
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Data
@Document(collection = "user_likes")
public class UserLike {
    /**
     * 用户ID
     */
    @Id
    private String userId;

    /**
     * 已点赞的诗词ID集合
     */
    private Set<String> likedPoemIds;

    /**
     * 已点赞的评论ID集合
     */
    private Set<String> likedCommentIds;

    // Getters and setters
}
