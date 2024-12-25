package com.sspu.nslike.service;

import com.sspu.nslike.exception.CustomException;
import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.model.UserLike;
import com.sspu.nslike.repository.LikeRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import com.sspu.nslike.repository.UserLikeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserLikeRepository userLikeRepository;

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    public PoemLike togglePoemLike(String poemId, String userId) {
        if (poemId == null || poemId.trim().isEmpty()) {
            throw new CustomException("诗词ID不能为空", 400);
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new CustomException("用户ID不能为空", 400);
        }

        try {
            // 获取诗词的点赞记录
            PoemLike poemLike = poemLikeRepository.findByPoemId(poemId)
                    .orElseGet(() -> {
                        PoemLike newPoemLike = new PoemLike();
                        newPoemLike.setPoemId(poemId);
                        newPoemLike.setLikeCount(0);
                        newPoemLike.setVisitCount(0);
                        newPoemLike.setLikedUserIds(new HashSet<>());
                        return newPoemLike;
                    });

            // 获取用户的点赞记录
            UserLike userLike = userLikeRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        UserLike newUserLike = new UserLike();
                        newUserLike.setUserId(userId);
                        newUserLike.setLikedPoemIds(new HashSet<>());
                        newUserLike.setLikedCommentIds(new HashSet<>());
                        return newUserLike;
                    });

            // 切换点赞状态
            if (poemLike.getLikedUserIds().contains(userId)) {
                poemLike.removeLike(userId);
                userLike.getLikedPoemIds().remove(poemId);
            } else {
                poemLike.addLike(userId);
                userLike.getLikedPoemIds().add(poemId);
            }

            // 保存更新
            userLikeRepository.save(userLike);
            return poemLikeRepository.save(poemLike);
        } catch (Exception e) {
            log.error("切换点赞状态失败: {}", e.getMessage());
            throw new CustomException("切换点赞状态失败: " + e.getMessage(), 500);
        }
    }

    public List<Comment> getCommentsByPoemId(String poemId) {
        if (poemId == null || poemId.trim().isEmpty()) {
            throw new CustomException("诗词ID不能为空", 400);
        }

        try {
            List<Comment> comments = likeRepository.findByPoemId(poemId);
            return comments != null ? comments : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取评论失败: {}", e.getMessage());
            throw new CustomException("获取评论失败: " + e.getMessage(), 500);
        }
    }

    public Comment addComment(Comment comment) {
        if (comment.getPoemId() == null || comment.getPoemId().trim().isEmpty()) {
            throw new CustomException("诗词ID不能为空", 400);
        }
        if (comment.getUserId() == null || comment.getUserId().trim().isEmpty()) {
            throw new CustomException("用户ID不能为空", 400);
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new CustomException("评论内容不能为空", 400);
        }

        try {
            comment.setLikeCount(0);
            comment.setCreateTime(LocalDateTime.now());
            comment.setLikedUserIds(new HashSet<>());
            comment.setReplies(new ArrayList<>());
            return likeRepository.save(comment);
        } catch (Exception e) {
            log.error("添加评论失败: {}", e.getMessage());
            throw new CustomException("添加评论失败: " + e.getMessage(), 500);
        }
    }

    public Comment toggleCommentLike(String commentId, String userId) {
        if (commentId == null || commentId.trim().isEmpty()) {
            throw new CustomException("评论ID不能为空", 400);
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new CustomException("用户ID不能为空", 400);
        }

        try {
            Comment comment = likeRepository.findById(commentId)
                    .orElseThrow(() -> new CustomException("评论不存在", 404));

            UserLike userLike = userLikeRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        UserLike newUserLike = new UserLike();
                        newUserLike.setUserId(userId);
                        newUserLike.setLikedPoemIds(new HashSet<>());
                        newUserLike.setLikedCommentIds(new HashSet<>());
                        return newUserLike;
                    });

            if (comment.hasLiked(userId)) {
                comment.removeLike(userId);
                userLike.getLikedCommentIds().remove(commentId);
            } else {
                comment.addLike(userId);
                userLike.getLikedCommentIds().add(commentId);
            }

            userLikeRepository.save(userLike);
            return likeRepository.save(comment);
        } catch (Exception e) {
            log.error("切换评论点赞状态失败: {}", e.getMessage());
            throw new CustomException("切换评论点赞状态失败: " + e.getMessage(), 500);
        }
    }

    public int getPoemLikeCount(String poemId) {
        return poemLikeRepository.findByPoemId(poemId)
                .map(PoemLike::getLikeCount)
                .orElse(0);
    }
}