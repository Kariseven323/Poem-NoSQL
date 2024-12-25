package com.sspu.nslike.service;

import com.sspu.nslike.exception.CustomException;
import com.sspu.nslike.model.Comment;
import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.model.UserLike;
import com.sspu.nslike.repository.LikeRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import com.sspu.nslike.repository.UserLikeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserLikeRepository userLikeRepository;
    @Autowired
    private PoemLikeRepository poemLikeRepository;

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

            // 如果是顶级评论
            if (comment.getParentId() == null) {
                return likeRepository.save(comment);
            }

            // 如果是子评论，查找父评论
            Comment parentComment = likeRepository.findById(comment.getParentId())
                    .orElseThrow(() -> new CustomException("父评论不存在", 404));

            // 将子评论添加到父评论的replies列表
            if (parentComment.getReplies() == null) {
                parentComment.setReplies(new ArrayList<>());
            }
            parentComment.getReplies().add(comment);

            // 保存父评论
            likeRepository.save(parentComment);
            return comment;
        } catch (Exception e) {
            throw new CustomException("添加评论失败: " + e.getMessage(), 500);
        }
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
        if (poemId == null || poemId.trim().isEmpty()) {
            throw new CustomException("诗词ID不能为空", 400);
        }

        try {
            // 获取所有评论
            List<Comment> allComments = likeRepository.findByPoemId(poemId);
            
            // 如果没有评论，返回空列表
            if (allComments == null || allComments.isEmpty()) {
                return new ArrayList<>();
            }

            // 构建评论树
            Map<String, Comment> commentMap = new HashMap<>();
            List<Comment> topLevelComments = new ArrayList<>();

            // 将所有评论放入map
            for (Comment comment : allComments) {
                commentMap.put(comment.getId(), comment);
                if (comment.getReplies() == null) {
                    comment.setReplies(new ArrayList<>());
                }
                if (comment.getParentId() == null) {
                    topLevelComments.add(comment);
                }
            }

            // 构建评论树
            for (Comment comment : allComments) {
                if (comment.getParentId() != null) {
                    Comment parent = commentMap.get(comment.getParentId());
                    if (parent != null) {
                        if (parent.getReplies() == null) {
                            parent.setReplies(new ArrayList<>());
                        }
                        parent.getReplies().add(comment);
                    }
                }
            }

            return topLevelComments;
        } catch (Exception e) {
            throw new CustomException("获取评论失败: " + e.getMessage(), 500);
        }
    }

    public Comment toggleCommentLike(String commentId, String userId) {
        // 获取评论
        Comment comment = likeRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("评论不存在", 404));

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
        if (userLike.getLikedCommentIds().contains(commentId)) {
            userLike.getLikedCommentIds().remove(commentId);
            comment.setLikeCount(comment.getLikeCount() - 1);
        } else {
            userLike.getLikedCommentIds().add(commentId);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }

        // 保存更新
        userLikeRepository.save(userLike);
        return likeRepository.save(comment);
    }

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
                poemLike.getLikedUserIds().remove(userId);
                poemLike.setLikeCount(Math.max(0, poemLike.getLikeCount() - 1));
                userLike.getLikedPoemIds().remove(poemId);
            } else {
                poemLike.getLikedUserIds().add(userId);
                poemLike.setLikeCount(poemLike.getLikeCount() + 1);
                userLike.getLikedPoemIds().add(poemId);
            }

            // 保存更新
            userLikeRepository.save(userLike);
            return poemLikeRepository.save(poemLike);
        } catch (Exception e) {
            throw new CustomException("切换点赞状态失败: " + e.getMessage(), 500);
        }
    }


    // MySQL 配置
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "zsh123";

    @PostConstruct
    public void initializePoemLikes() {
        try (Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            // Step 1: 从 MySQL 加载所有诗词 ID
            List<String> poemIds = fetchAllPoemIds(connection);

            // Step 2: 从 MongoDB 加载现有的诗词 ID
            List<PoemLike> existingPoemLikes = poemLikeRepository.findAll();

            // Step 3: 确保所有现有记录都有 visitCount 字段
            for (PoemLike poemLike : existingPoemLikes) {
                if (poemLike.getVisitCount() == 0) {
                    poemLike.setVisitCount(0); // 确保字段存在
                    poemLikeRepository.save(poemLike);
                }
            }

            // Step 4: 插入新记录
            Set<String> existingPoemIds = existingPoemLikes.stream()
                    .map(PoemLike::getPoemId)
                    .collect(Collectors.toSet());

            List<PoemLike> newPoemLikes = poemIds.stream()
                    .filter(poemId -> !existingPoemIds.contains(poemId))
                    .map(poemId -> {
                        PoemLike poemLike = new PoemLike();
                        poemLike.setPoemId(poemId);
                        poemLike.setLikeCount(0);
                        poemLike.setVisitCount(0); // 初始化访问量
                        poemLike.setLikedUserIds(new HashSet<>());
                        return poemLike;
                    })
                    .collect(Collectors.toList());

            if (!newPoemLikes.isEmpty()) {
                poemLikeRepository.saveAll(newPoemLikes);
                System.out.println("新增诗词点赞集合: " + newPoemLikes.size());
            } else {
                System.out.println("所有诗词集合已存在，无需更新");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("初始化诗词点赞集合失败: " + e.getMessage());
        }
    }

    private List<String> fetchAllPoemIds(Connection connection) throws Exception {
        List<String> poemIds = new ArrayList<>();
        String sql = "SELECT id FROM ancient_poetry"; // 假设诗词表名为 'poems'
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                poemIds.add(resultSet.getString("id"));
            }
        }
        return poemIds;
    }

    public PoemLike incrementVisitCount(String poemId) {
        PoemLike poemLike = poemLikeRepository.findByPoemId(poemId)
                .orElseThrow(() -> new CustomException("诗词不存在", 404));

        // 更新访问量
        poemLike.setVisitCount(poemLike.getVisitCount() + 1);
        return poemLikeRepository.save(poemLike);
    }

    /**
     * 获取诗词的点赞数量
     * @param poemId 诗词ID
     * @return 返回点赞数量
     */
    public int getPoemLikeCount(String poemId) {
        return poemLikeRepository.findByPoemId(poemId)
                .map(PoemLike::getLikeCount)
                .orElse(0);
    }

}