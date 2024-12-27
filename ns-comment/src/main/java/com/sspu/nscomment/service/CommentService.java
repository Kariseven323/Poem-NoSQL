package com.sspu.nscomment.service;

import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.entity.CommentNode;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nscomment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // 初始化 MongoDB 中的评论数据
    public void initializeComments() {
        ancientPoetryRepository.findAll().forEach(poetry -> {
            if (!commentRepository.existsByPoemId(poetry.getId())) {
                Comment comment = new Comment();
                comment.setPoemId(poetry.getId());
                commentRepository.save(comment);
            }
        });
    }

    // 添加评论
    public Comment addComment(String poemId, String userId, String content, String parentId) {
        try {
            System.out.println("添加评论，poemId=" + poemId + ", userId=" + userId + ", content=" + content + ", parentId=" + parentId);

            Optional<Comment> optionalComment = commentRepository.findByPoemId(poemId);

            if (optionalComment.isPresent()) {
                Comment comment = optionalComment.get();
                CommentNode newComment = new CommentNode(userId, content);

                if (parentId == null) {
                    System.out.println("添加一级评论：" + newComment);
                    // 使用 MongoDB 的 $push 操作追加评论
                    Query query = new Query(Criteria.where("poemId").is(poemId));
                    Update update = new Update().push("comments", newComment);
                    mongoTemplate.updateFirst(query, update, Comment.class);
                } else {
                    System.out.println("尝试添加嵌套评论，父评论ID：" + parentId);
                    boolean found = comment.findAndAddReply(parentId, newComment);
                    if (!found) {
                        throw new RuntimeException("未找到父评论，ID=" + parentId);
                    }
                    // 保存整个评论对象
                    commentRepository.save(comment);
                }
                return comment;
            } else {
                throw new RuntimeException("诗词不存在，无法添加评论！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加评论失败：" + e.getMessage());
        }
    }

    // 根据 poemId 获取评论
    public Comment getCommentsByPoemId(String poemId) {
        Optional<Comment> optionalComment = commentRepository.findByPoemId(poemId);
        return optionalComment.orElse(null);
    }

}
