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
    private String id;
    @Field("poemId")
    private String poemId;
    @Field("comments")
    private List<CommentNode> comments = new ArrayList<>();

    // 查找父评论并添加嵌套评论
    public boolean findAndAddReply(String parentId, CommentNode reply) {
        for (CommentNode commentNode : comments) {
            System.out.println("正在检查评论节点ID：" + commentNode.getId());
            if (commentNode.getId().equals(parentId)) {
                System.out.println("找到父评论ID：" + parentId + "，添加回复：" + reply);
                commentNode.getReplies().add(reply);
                return true;
            }
            boolean found = commentNode.findAndAddReply(parentId, reply);
            if (found) return true; // 递归中找到后直接返回
        }
        return false; // 未找到父评论
    }

}
