package com.sspu.nscomment.service;

import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.entity.CommentNode;
import com.sspu.nscomment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // 添加评论
    public Comment addComment(String poemId, String userId, String content, String parentId) {
        Optional<Comment> optionalComment = commentRepository.findByPoemId(poemId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            CommentNode newComment = new CommentNode(userId, content);

            if (parentId == null) {
                // 添加一级评论
                Query query = new Query(Criteria.where("poemId").is(poemId));
                Update update = new Update().push("comments", newComment);
                mongoTemplate.updateFirst(query, update, Comment.class);
            } else {
                // 添加回复
                boolean added = comment.findAndAddReply(parentId, newComment);
                if (added) {
                    mongoTemplate.save(comment);
                } else {
                    throw new RuntimeException("未找到父评论节点");
                }
            }
            return comment;
        } else {
            throw new RuntimeException("未找到目标诗词评论");
        }
    }

    // 点赞或取消点赞
    public void toggleLike(String poemId, String commentId, boolean isLike) {
        Query query = new Query(Criteria.where("poemId").is(poemId));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        if (comment != null) {
            boolean updated = comment.findAndToggleLike(commentId, isLike);
            if (updated) {
                mongoTemplate.save(comment); // 保存更新后的评论数据
            } else {
                throw new RuntimeException("未找到目标评论节点");
            }
        } else {
            throw new RuntimeException("未找到目标诗词评论");
        }
    }

    // 获取按点赞数排序的评论
    public List<CommentNode> getSortedComments(String poemId) {
        Query query = new Query(Criteria.where("poemId").is(poemId));
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        if (comment != null) {
            List<CommentNode> allComments = new ArrayList<>();
            flattenComments(comment.getComments(), allComments);
            allComments.sort((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()));
            return allComments;
        } else {
            throw new RuntimeException("未找到目标诗词评论");
        }
    }

    // 展开评论的层级结构
    private void flattenComments(List<CommentNode> comments, List<CommentNode> allComments) {
        for (CommentNode comment : comments) {
            allComments.add(comment);
            flattenComments(comment.getReplies(), allComments);
        }
    }
}
