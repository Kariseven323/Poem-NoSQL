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


    /**
     * 为诗词添加评论
     * @param comment 评论对象，必须包含诗词ID（poemId）
     * @return 返回保存的评论
     */
    public Comment addCommentToPoem(Comment comment) {
        if (comment.getPoemId() == null || comment.getPoemId().isEmpty()) {
            throw new CustomException("诗词ID不能为空", 400);
        }

        comment.setLikeCount(0);
        comment.setCreateTime(LocalDateTime.now());

        // 如果是顶级评论
        if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
            return likeRepository.save(comment);
        }

        // 如果是子评论，查找父评论
        Comment parentComment = likeRepository.findById(comment.getParentId())
                .orElseThrow(() -> new CustomException("父评论不存在", 404));

        // 将子评论添加到父评论的 replies 列表
        parentComment.getReplies().add(comment);

        // 保存父评论（级联保存子评论）
        likeRepository.save(parentComment);

        return comment;
    }

    /**
     * 获取某首诗词的所有评论（按点赞量从大到小排序，包含嵌套结构）
     * @param poemId 诗词ID
     * @return 返回顶级评论及其嵌套子评论列表
     */
    public List<Comment> getCommentsForPoem(String poemId) {
        // 获取诗词对应的所有评论
        List<Comment> allComments = likeRepository.findByPoemId(poemId);

        // 构造评论的顶级和嵌套结构
        Map<String, Comment> commentMap = new HashMap<>();
        List<Comment> topLevelComments = new ArrayList<>();

        for (Comment comment : allComments) {
            commentMap.put(comment.getId(), comment);

            // 如果是顶级评论，加入顶级列表
            if (comment.getParentId() == null) {
                topLevelComments.add(comment);
            }
        }

        // 构建嵌套评论结构
        for (Comment comment : allComments) {
            if (comment.getParentId() != null) {
                Comment parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    parentComment.getReplies().add(comment);
                }
            }
        }

        // 对顶级评论和嵌套子评论按点赞数排序
        topLevelComments.sort((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()));
        for (Comment topComment : topLevelComments) {
            sortRepliesByLikeCount(topComment);
        }

        return topLevelComments;
    }

    // 辅助方法：递归对子评论进行排序
    private void sortRepliesByLikeCount(Comment comment) {
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            comment.getReplies().sort((c1, c2) -> Integer.compare(c2.getLikeCount(), c1.getLikeCount()));
            for (Comment reply : comment.getReplies()) {
                sortRepliesByLikeCount(reply); // 递归排序
            }
        }
    }

    /**
     * 切换评论的点赞状态
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 返回更新后的评论
     */
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

    /**
     * 切换诗词的点赞状态
     * @param poemId 诗词ID
     * @param userId 用户ID
     * @return 返回更新后的诗词点赞记录
     */
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


    /**
     * 初始化诗词的点赞数据
     */
    @PostConstruct
    public void initializePoemLikes() {
        try (Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            // 从 MySQL 加载所有诗词 ID
            List<String> poemIds = fetchAllPoemIds(connection);

            // 从 MongoDB 加载现有的诗词点赞记录
            List<PoemLike> existingPoemLikes = poemLikeRepository.findAll();

            Set<String> existingPoemIds = existingPoemLikes.stream()
                    .map(PoemLike::getPoemId)
                    .collect(Collectors.toSet());

            List<PoemLike> newPoemLikes = poemIds.stream()
                    .filter(poemId -> !existingPoemIds.contains(poemId))
                    .map(poemId -> {
                        PoemLike poemLike = new PoemLike();
                        poemLike.setPoemId(poemId);
                        poemLike.setLikeCount(0);
                        poemLike.setVisitCount(0);
                        poemLike.setLikedUserIds(new HashSet<>());
                        return poemLike;
                    })
                    .collect(Collectors.toList());

            if (!newPoemLikes.isEmpty()) {
                poemLikeRepository.saveAll(newPoemLikes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> fetchAllPoemIds(Connection connection) throws Exception {
        List<String> poemIds = new ArrayList<>();
        String sql = "SELECT id FROM ancient_poetry";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                poemIds.add(resultSet.getString("id"));
            }
        }
        return poemIds;
    }

    /**
     * 增加诗词访问量
     * @param poemId 诗词ID
     * @return 返回更新后的诗词点赞记录
     */
    public PoemLike incrementVisitCount(String poemId) {
        PoemLike poemLike = poemLikeRepository.findByPoemId(poemId)
                .orElseThrow(() -> new CustomException("诗词不存在", 404));
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


    // MySQL 配置
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "cdj123";
}
