package com.sspu.nscomment.controller;

import com.sspu.nscomment.entity.CommentNode;
import com.sspu.nscomment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 添加评论
    @PostMapping("/{poemId}/add")
    public ResponseEntity<?> addComment(
            @PathVariable("poemId") String poemId,
            @RequestParam("userId") String userId,
            @RequestParam("content") String content,
            @RequestParam(value = "parentId", required = false) String parentId) {
        try {
            commentService.addComment(poemId, userId, content, parentId);
            return ResponseEntity.ok("评论添加成功！");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 点赞或取消点赞
    @PostMapping("/{poemId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable("poemId") String poemId,
            @RequestParam("commentId") String commentId,
            @RequestParam("isLike") boolean isLike) {
        try {
            commentService.toggleLike(poemId, commentId, isLike);
            return ResponseEntity.ok("点赞操作成功！");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 获取按点赞排序的评论
    @GetMapping("/{poemId}/sorted-comments")
    public ResponseEntity<?> getSortedComments(@PathVariable("poemId") String poemId) {
        try {
            List<CommentNode> sortedComments = commentService.getSortedComments(poemId);
            return ResponseEntity.ok(sortedComments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
