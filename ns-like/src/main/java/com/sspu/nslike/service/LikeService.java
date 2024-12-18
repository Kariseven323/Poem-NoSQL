package com.sspu.nslike.service;

import com.sspu.nslike.exception.CustomException;
import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.UserLike;
import com.sspu.nslike.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public Comment addComment(Comment comment) {
        comment.setLikeCount(0);
        comment.setCreatedAt(java.time.LocalDateTime.now());
        return likeRepository.save(comment);
    }

    public List<Comment> getCommentsByPoemId(String poemId) {
        return likeRepository.findByPoemIdOrderByLikeCountDesc(poemId);
    }

    public Comment likeComment(String commentId, String userId) {
        Optional<Comment> commentOpt = likeRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Comment not found with ID: " + commentId);
        }

        // 业务逻辑处理
        Comment comment = commentOpt.get();
        comment.setLikeCount(comment.getLikeCount() + 1);
        return likeRepository.save(comment);
    }

    public Comment unlikeComment(String commentId, String userId) {
        Comment comment = likeRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setLikeCount(comment.getLikeCount() - 1);
        return likeRepository.save(comment);
    }
}
