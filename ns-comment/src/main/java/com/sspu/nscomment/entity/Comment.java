package com.sspu.nscomment.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "comments")
@Data
public class Comment {
    @Id
    private String id; // 唯一标识
    @Field("poemId")
    private String poemId; // 所属诗词ID
    @Field("comments")
    private List<CommentNode> comments = new ArrayList<>(); // 一级评论列表

    // 递归查找父评论并添加嵌套回复
    public boolean findAndAddReply(String parentId, CommentNode reply) {
        for (CommentNode commentNode : comments) {
            if (commentNode.getId().equals(parentId)) {
                commentNode.getReplies().add(reply);
                return true;
            }
            if (commentNode.toggleLike(parentId, true)) {
                return true;
            }
        }
        return false;
    }

    // 递归查找并对评论节点进行点赞或取消点赞
    public boolean findAndToggleLike(String targetId, boolean isLike) {
        for (CommentNode commentNode : comments) {
            if (commentNode.toggleLike(targetId, isLike)) {
                return true;
            }
        }
        return false;
    }
}
