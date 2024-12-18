package com.sspu.nslike.controller;

import com.sspu.nslike.model.Comment;
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

    @PostMapping("/comments/{commentId}/like")
    public Comment toggleLike(@PathVariable("commentId") String commentId, @RequestParam("userId") String userId) {
        return likeService.toggleLike(commentId, userId);
    }

}
