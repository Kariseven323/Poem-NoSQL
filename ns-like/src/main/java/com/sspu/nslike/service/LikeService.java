package com.sspu.nslike.service;

import com.sspu.nslike.exception.CustomException;
import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.UserLike;
import com.sspu.nslike.repository.LikeRepository;
import com.sspu.nslike.repository.UserLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserLikeRepository userLikeRepository; // 确保注入正确

    public Comment addComment(Comment comment) {
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        return likeRepository.save(comment);
    }

    public List<Comment> getCommentsByPoemId(String poemId) {
        return likeRepository.findByPoemIdOrderByLikeCountDesc(poemId);
    }

    public Comment toggleLike(String commentId, String userId) {
        // 查询评论是否存在
        Comment comment = likeRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Comment not found with ID: " + commentId));

        // 检查用户是否已点赞
        Optional<UserLike> userLikeOpt = userLikeRepository.findByUserIdAndTargetId(userId, commentId);

        if (userLikeOpt.isPresent()) {
            // 如果已点赞，则取消点赞并删除记录
            userLikeRepository.delete(userLikeOpt.get());
            comment.setLikeCount(comment.getLikeCount() - 1);
        } else {
            // 如果未点赞，则添加点赞记录
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setTargetId(commentId);
            userLikeRepository.save(userLike);

            comment.setLikeCount(comment.getLikeCount() + 1);
        }

        // 保存评论的点赞数量更新
        return likeRepository.save(comment);
    }

}
