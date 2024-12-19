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
            Set<String> existingPoemIds = poemLikeRepository.findAll()
                    .stream()
                    .map(PoemLike::getPoemId)
                    .collect(Collectors.toSet());

            // Step 3: 计算需要插入的诗词 ID
            List<PoemLike> newPoemLikes = poemIds.stream()
                    .filter(poemId -> !existingPoemIds.contains(poemId))
                    .map(poemId -> {
                        PoemLike poemLike = new PoemLike();
                        poemLike.setPoemId(poemId);
                        poemLike.setLikeCount(0);
                        poemLike.setLikedUserIds(new HashSet<>());
                        return poemLike;
                    })
                    .collect(Collectors.toList());

            // Step 4: 批量插入 MongoDB
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
}