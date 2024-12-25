package com.sspu.nslike.controller;

import com.sspu.nslike.model.Comment;
import com.sspu.nslike.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论功能控制器
 * 处理所有与评论相关的HTTP请求
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private LikeService likeService;

    /**
     * 添加新的评论
     * @param comment 评论对象
     * @return 返回保存的评论对象
     */
    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(likeService.addCommentToPoem(comment));
    }

    /**
     * 添加子评论（回复父评论）
     * @param parentId 父评论ID
     * @param reply 子评论对象
     * @return 返回添加的子评论对象
     */
    @PostMapping("/{parentId}/replies")
    public ResponseEntity<Comment> addReplyToParent(@PathVariable String parentId, @RequestBody Comment reply) {
        reply.setParentId(parentId); // 设置父评论ID
        return ResponseEntity.ok(likeService.addCommentToPoem(reply));
    }

    /**
     * 根据诗词ID获取评论列表
     * @param poemId 诗词ID
     * @return 返回包含嵌套结构的评论列表
     */
    @GetMapping("/poems/{poemId}")
    public ResponseEntity<List<Comment>> getCommentsByPoemId(@PathVariable String poemId) {
        return ResponseEntity.ok(likeService.getCommentsForPoem(poemId));
    }

    /**
     * 点赞评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 返回更新后的评论
     */
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<Comment> toggleCommentLike(@PathVariable String commentId, @RequestParam String userId) {
        return ResponseEntity.ok(likeService.toggleCommentLike(commentId, userId));
    }
}
