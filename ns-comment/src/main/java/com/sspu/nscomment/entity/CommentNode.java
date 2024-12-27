package com.sspu.nscomment.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CommentNode {
    private String id;
    private String userId;
    private String content;
    private List<CommentNode> replies = new ArrayList<>();

    public CommentNode(String userId, String content) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.content = content;
    }

    public boolean findAndAddReply(String parentId, CommentNode reply) {
        for (CommentNode replyNode : replies) {
            System.out.println("正在检查子评论节点ID：" + replyNode.getId());
            if (replyNode.getId().equals(parentId)) {
                System.out.println("找到父评论ID：" + parentId + "，添加嵌套回复：" + reply);
                replyNode.getReplies().add(reply);
                return true;
            }
            boolean found = replyNode.findAndAddReply(parentId, reply);
            if (found) return true; // 递归中找到后直接返回
        }
        return false; // 未找到父评论
    }
}
