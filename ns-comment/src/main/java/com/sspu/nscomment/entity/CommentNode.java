package com.sspu.nscomment.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CommentNode {
    private String id; // 唯一标识
    private String userId; // 用户ID
    private String content; // 评论内容
    private int likeCount = 0; // 点赞数
    private List<CommentNode> replies = new ArrayList<>(); // 子评论

    // 构造函数
    public CommentNode(String userId, String content) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.content = content;
    }

    // 递归查找并对评论节点进行点赞或取消点赞
    public boolean toggleLike(String targetId, boolean isLike) {
        if (this.id.equals(targetId)) {
            this.likeCount += isLike ? 1 : -1;
            return true;
        }
        for (CommentNode reply : replies) {
            if (reply.toggleLike(targetId, isLike)) {
                return true;
            }
        }
        return false;
    }
}
