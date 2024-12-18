package com.sspu.nslike.service;

import com.sspu.nslike.exception.CustomException;
import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.model.UserLike;
import com.sspu.nslike.repository.LikeRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import com.sspu.nslike.repository.UserLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserLikeRepository userLikeRepository;
    @Autowired
    private PoemLikeRepository poemLikeRepository;

    public Comment addComment(Comment comment) {
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());

        // 如果是顶级评论
        if (comment.getParentId() == null) {
            return likeRepository.save(comment); // 直接保存
        }

        // 如果是子评论
        Comment parentComment = likeRepository.findById(comment.getParentId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Parent comment not found"));

        // 将子评论添加到父评论的 replies 列表
        parentComment.getReplies().add(comment);

        // 保存父评论（级联保存子评论）
        likeRepository.save(parentComment);

        return comment; // 返回子评论
    }

    private void addReplyToParent(Comment parent, Comment reply) {
        // 如果该评论的 ID 与目标 parentId 匹配，则添加子评论
        if (parent.getId().equals(reply.getParentId())) {
            parent.getReplies().add(reply);
        } else {
            // 否则在父评论的子评论中继续递归寻找
            for (Comment child : parent.getReplies()) {
                addReplyToParent(child, reply);
            }
        }
    }

    public List<Comment> getCommentsByPoemId(String poemId) {
        // 获取所有评论
        List<Comment> allComments = likeRepository.findByPoemId(poemId);

        // 构造顶级评论和嵌套评论
        Map<String, Comment> commentMap = new HashMap<>();
        List<Comment> topLevelComments = new ArrayList<>();

        // 先将所有评论存入 Map，方便查找
        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);

            // 如果没有父评论（即顶级评论），加入顶级评论列表
            if (comment.getParentId() == null) {
                topLevelComments.add(comment);
            }
        }

        // 构建嵌套结构
        for (Comment comment : allComments) {
            if (comment.getParentId() != null) {
                // 找到父评论并添加到其 replies 列表中
                Comment parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                }
            }
        }

        return topLevelComments; // 返回顶级评论及其嵌套
    }

    public Comment toggleCommentLike(String commentId, String userId) {
        Comment comment = likeRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Comment not found"));

        // 判断用户是否已点赞
        if (comment.getLikedUserIds().contains(userId)) {
            // 如果已点赞，则执行取消点赞
            comment.getLikedUserIds().remove(userId);
            comment.setLikeCount(comment.getLikeCount() - 1);
        } else {
            // 如果未点赞，则执行点赞
            comment.getLikedUserIds().add(userId);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }

        // 保存更新后的评论
        return likeRepository.save(comment);
    }

    public PoemLike togglePoemLike(String poemId, String userId) {
        PoemLike poemLike = poemLikeRepository.findById(poemId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Poem not found"));

        // 判断用户是否已点赞
        if (poemLike.getLikedUserIds().contains(userId)) {
            // 如果已点赞，则执行取消点赞
            poemLike.getLikedUserIds().remove(userId);
            poemLike.setLikeCount(poemLike.getLikeCount() - 1);
        } else {
            // 如果未点赞，则执行点赞
            poemLike.getLikedUserIds().add(userId);
            poemLike.setLikeCount(poemLike.getLikeCount() + 1);
        }

        // 保存更新后的诗词点赞信息
        return poemLikeRepository.save(poemLike);
    }

}
