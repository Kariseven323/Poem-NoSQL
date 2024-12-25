package com.sspu.nslike.controller;

import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点赞功能控制器
 * 处理所有与点赞相关的HTTP请求
 * 包括诗词点赞、评论点赞等功能
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Slf4j
@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LikeController {

    /**
     * 点赞服务接口
     */
    @Autowired
    private LikeService likeService;

    /**
     * 处理用户对诗词的点赞请求
     * @param poemId 诗词ID
     * @param userId 用户ID
     * @return 返回点赞操作的结果
     */
    @PostMapping("/poems/{poemId}/toggle-like")
    public ResponseEntity<?> togglePoemLike(@PathVariable String poemId, @RequestParam String userId) {
        log.info("Toggling like for poem: {}, user: {}", poemId, userId);
        try {
            PoemLike result = likeService.togglePoemLike(poemId, userId);
            log.info("Successfully toggled like for poem: {}", poemId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error toggling like for poem: {}", poemId, e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }
    }

    /**
     * 获取诗词的点赞数量
     * @param poemId 诗词ID
     * @return 返回点赞数量
     */
    @GetMapping("/poems/{poemId}/count")
    public ResponseEntity<Integer> getPoemLikeCount(@PathVariable String poemId) {
        return ResponseEntity.ok(likeService.getPoemLikeCount(poemId));
    }

    /**
     * 获取诗词的评论
     * @param poemId 诗词ID
     * @return 返回诗词的评论列表
     */
    @GetMapping("/poems/{poemId}/comments")
    public ResponseEntity<?> getComments(@PathVariable String poemId) {
        log.info("Getting comments for poem: {}", poemId);
        try {
            List<Comment> comments = likeService.getCommentsByPoemId(poemId);
            log.info("Successfully retrieved {} comments for poem: {}", comments.size(), poemId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error getting comments for poem: {}", poemId, e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }
    }

    /**
     * 添加评论
     * @param comment 评论对象
     * @return 返回添加的评论
     */
    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        log.info("Adding comment for poem: {}", comment.getPoemId());
        try {
            Comment result = likeService.addComment(comment);
            log.info("Successfully added comment for poem: {}", comment.getPoemId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error adding comment for poem: {}", comment.getPoemId(), e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }
    }

    /**
     * 处理用户对评论的点赞请求
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 返回点赞操作的结果
     */
    @PostMapping("/comments/{commentId}/toggle-like")
    public ResponseEntity<?> toggleCommentLike(@PathVariable String commentId, @RequestParam String userId) {
        log.info("Toggling like for comment: {}, user: {}", commentId, userId);
        try {
            Comment result = likeService.toggleCommentLike(commentId, userId);
            log.info("Successfully toggled like for comment: {}", commentId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error toggling like for comment: {}", commentId, e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
        }
    }
}

class ErrorResponse {
    private int code;
    private String message;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
