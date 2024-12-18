package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Comment> replies; // 子评论列表
}