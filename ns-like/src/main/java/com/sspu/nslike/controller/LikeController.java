package com.sspu.nslike.controller;

import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/comments")
    public Comment addComment(@RequestBody Comment comment) {
        return likeService.addComment(comment);
    }

    @GetMapping("/comments/{poemId}")
    public List<Comment> getCommentsByPoemId(@PathVariable String poemId) {
        return likeService.getCommentsByPoemId(poemId);
    }

    @PostMapping("/comments/{commentId}/toggle-like")
    public Comment toggleCommentLike(@PathVariable String commentId, @RequestParam String userId) {
        return likeService.toggleCommentLike(commentId, userId);
    }

    @PostMapping("/poems/{poemId}/toggle-like")
    public PoemLike togglePoemLike(@PathVariable String poemId, @RequestParam String userId) {
        return likeService.togglePoemLike(poemId, userId);
    }
}
