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
        comment.setLikes(0);
        comment.setCreateTime(LocalDateTime.now());

        // 如果是顶级评论
        if (comment.getParentId() == null) {
            return likeRepository.save(comment); // 直接保存
        }

        // 如果是子评论
        Comment parentComment = likeRepository.findById(comment.getParentId())
                .orElseThrow(() -> new CustomException("父评论不存在", 404));

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
            comment.setLikes(comment.getLikes() - 1);
        } else {
            userLike.getLikedCommentIds().add(commentId);
            comment.setLikes(comment.getLikes() + 1);
        }

        // 保存更新
        userLikeRepository.save(userLike);
        return likeRepository.save(comment);
    }

    public PoemLike togglePoemLike(String poemId, String userId) {
        // 获取诗词点赞记录
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
        if (userLike.getLikedPoemIds().contains(poemId)) {
            userLike.getLikedPoemIds().remove(poemId);
            poemLike.setLikeCount(poemLike.getLikeCount() - 1);
            poemLike.getLikedUserIds().remove(userId);
        } else {
            userLike.getLikedPoemIds().add(poemId);
            poemLike.setLikeCount(poemLike.getLikeCount() + 1);
            poemLike.getLikedUserIds().add(userId);
        }

        // 保存更新
        userLikeRepository.save(userLike);
        return poemLikeRepository.save(poemLike);
    }


    // MySQL 配置
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "cdj123";

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