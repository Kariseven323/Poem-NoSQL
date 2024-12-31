package com.sspu.nscomment.service;

import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.entity.CommentNode;
import com.sspu.nscomment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String COMMENT_CACHE_PREFIX = "comments:";

    // 异步更新 Redis 缓存
    @Async("taskExecutor")
    public void updateRedisCache(String poemId, Comment comment) {
        redisTemplate.opsForValue().set(COMMENT_CACHE_PREFIX + poemId, comment, 10, TimeUnit.MINUTES);
    }

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

            // 异步更新 Redis 缓存
            updateRedisCache(poemId, comment);

            return comment;
        } else {
            throw new RuntimeException("未找到目标诗词评论");
        }
    }

    // 点赞或取消点赞
    public void toggleLike(String poemId, String commentId, boolean isLike) {
        // 检查缓存中是否有数据
        String cacheKey = COMMENT_CACHE_PREFIX + poemId;
        Comment comment = (Comment) redisTemplate.opsForValue().get(cacheKey);

        if (comment == null) {
            // 缓存未命中，从数据库中查询
            Query query = new Query(Criteria.where("poemId").is(poemId));
            comment = mongoTemplate.findOne(query, Comment.class);
            if (comment == null) {
                throw new RuntimeException("未找到目标诗词评论");
            }
        }

        boolean updated = comment.findAndToggleLike(commentId, isLike);
        if (updated) {
            mongoTemplate.save(comment);
            // 更新缓存
            redisTemplate.opsForValue().set(cacheKey, comment, 10, TimeUnit.MINUTES);
        } else {
            throw new RuntimeException("未找到目标评论节点");
        }
    }

    // 获取按点赞数排序的评论
    public List<CommentNode> getSortedComments(String poemId) {
        // 检查缓存
        String cacheKey = COMMENT_CACHE_PREFIX + poemId;
        Comment comment = (Comment) redisTemplate.opsForValue().get(cacheKey);

        if (comment == null) {
            // 缓存未命中，从数据库中查询
            Query query = new Query(Criteria.where("poemId").is(poemId));
            comment = mongoTemplate.findOne(query, Comment.class);
            if (comment == null) {
                throw new RuntimeException("未找到目标诗词评论");
            }
            // 缓存结果
            redisTemplate.opsForValue().set(cacheKey, comment, 10, TimeUnit.MINUTES);
        }

        List<CommentNode> allComments = new ArrayList<>();
        flattenComments(comment.getComments(), allComments);
        allComments.sort((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()));
        return allComments;
    }

    // 展开评论的层级结构
    private void flattenComments(List<CommentNode> comments, List<CommentNode> allComments) {
        for (CommentNode comment : comments) {
            allComments.add(comment);
            flattenComments(comment.getReplies(), allComments);
        }
    }
}
