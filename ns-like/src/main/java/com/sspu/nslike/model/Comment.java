package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String id; // 评论ID
    private String poemId; // 对应诗词ID
    private String parentId; // 父评论ID，如果是顶级评论则为 null
    private String userId; // 评论用户ID
    private String content; // 评论内容
    private int likeCount; // 点赞数量
    private LocalDateTime createdAt; // 创建时间

    // 保存点赞过该评论的用户ID
    private Set<String> likedUserIds = new HashSet<>();

    // 子评论列表
    private List<Comment> replies = new ArrayList<>();
}
