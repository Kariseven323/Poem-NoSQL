package com.sspu.nscomment.listener;

import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisExpiredEventListener {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Redis 过期事件的回调方法
     *
     * @param message Redis 事件消息
     */
    public void onMessage(String message) {
        try {
            System.out.println("Redis Key 过期事件触发，Key: " + message);

            // 从过期的 Redis Key 中解析 poemId
            String poemId = message.replace("comments:", "");

            // 根据 poemId 从 MongoDB 查询数据
            Comment comment = commentRepository.findByPoemId(poemId)
                    .orElseThrow(() -> new RuntimeException("未找到对应的评论结构，PoemId: " + poemId));

            // 更新 MongoDB 的数据逻辑（示例：更新评论统计数据等）
            comment.setComments(comment.getComments()); // 示例：触发更新操作
            mongoTemplate.save(comment);

            System.out.println("MongoDB 数据已更新，PoemId: " + poemId);
        } catch (Exception e) {
            System.err.println("处理 Redis 过期事件时发生错误: " + e.getMessage());
        }
    }
}
