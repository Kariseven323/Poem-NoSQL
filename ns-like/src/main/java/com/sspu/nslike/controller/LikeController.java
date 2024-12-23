package com.sspu.nslike.controller;

import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.service.LikeService;
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
@RestController
@RequestMapping("/api/likes")
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
        return ResponseEntity.ok(likeService.togglePoemLike(poemId, userId));
    }

    /**
     * 处理用户对评论的点赞请求
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 返回点赞操作的结果
     */
    @PostMapping("/comments/{commentId}/toggle-like")
    public ResponseEntity<?> toggleCommentLike(@PathVariable String commentId, @RequestParam String userId) {
        return ResponseEntity.ok(likeService.toggleCommentLike(commentId, userId));
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
}
