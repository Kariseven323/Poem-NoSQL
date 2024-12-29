package com.sspu.nscomment.controller;

import com.sspu.nscomment.entity.Comment;
import com.sspu.nscomment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 初始化数据
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeComments() {
        try {
            commentService.initializeComments();
            return ResponseEntity.ok("评论数据初始化成功！");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("初始化失败：" + e.getMessage());
        }
    }

    // 添加评论
    @PostMapping("/{poemId}/add")
    public ResponseEntity<?> addComment(
            @PathVariable("poemId") String poemId,
            @RequestParam("userId") String userId,
            @RequestParam("content") String content,
            @RequestParam(value = "parentId", required = false) String parentId) {
        try {
            System.out.println("收到添加评论请求，poemId=" + poemId + ", userId=" + userId + ", parentId=" + parentId);
            Comment comment = commentService.addComment(poemId, userId, content, parentId);
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("请求失败：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    // 获取评论
    @GetMapping("/{poemId}")
    public ResponseEntity<?> getComments(@PathVariable("poemId") String poemId) {
        try {
            System.out.println("收到获取评论请求，poemId=" + poemId);
            Comment comment = commentService.getCommentsByPoemId(poemId);
            if (comment != null) {
                return ResponseEntity.ok(comment);
            } else {
                return ResponseEntity.status(404).body("未找到该诗词的评论！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    @PostMapping("/{poemId}/{commentId}/like")
    public ResponseEntity<String> likeOrUnlikeComment(
            @PathVariable("poemId") String poemId,
            @PathVariable("commentId") String commentId,
            @RequestParam("userId") String userId) {
        try {
            boolean liked = commentService.likeOrUnlikeComment(poemId, commentId, userId);
            return ResponseEntity.ok(liked ? "点赞成功！" : "取消点赞成功！");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("请求失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    @GetMapping("/{poemId}/sorted-by-likes")
    public ResponseEntity<?> getCommentsSortedByLikes(@PathVariable("poemId") String poemId) {
        try {
            Comment comment = commentService.getCommentsSortedByLikes(poemId);
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("未找到该诗词的评论：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
