package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_likes")
public class UserLike {
    @Id
    private String id;
    private String targetId; // 点赞目标ID（评论ID或诗词ID）
    private String userId; // 用户ID
}
