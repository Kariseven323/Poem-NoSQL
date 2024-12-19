package com.sspu.nsrank.service;

import com.sspu.nslike.model.PoemLike;
import com.sspu.nslike.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "cdj123";

    /**
     * 获取访问量前50的诗词
     */
    public List<PoemLike> getTopVisitedPoems() {
        return poemLikeRepository.findAll()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.getVisitCount(), p1.getVisitCount())) // 按访问量降序排序
                .limit(50) // 限制返回50条
                .collect(Collectors.toList());
    }

    /**
     * 获取点赞量前50的诗词
     */
    public List<PoemLike> getTopLikedPoems() {
        return poemLikeRepository.findAll()
                .stream()
                .sorted((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount())) // 按点赞量降序排序
                .limit(50) // 限制返回50条
                .collect(Collectors.toList());
    }

    /**
     * 从 Redis 获取访问量前50的诗词
     */
    public Object getCachedTopVisitedPoems() {
        return redisTemplate.opsForValue().get("topVisited");
    }

    /**
     * 从 Redis 获取点赞量前50的诗词
     */
    public Object getCachedTopLikedPoems() {
        return redisTemplate.opsForValue().get("topLiked");
    }

    // 从 MySQL 查询诗词详细信息
    public String fetchPoemDetailFromMySQL(String poemId) {
        String detail = "";
        try (Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            String sql = "SELECT detail FROM ancient_poetry WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, poemId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    detail = resultSet.getString("detail");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    /**
     * 将数据存入 Redis
     */
    public void updateRedisCache() {
        List<PoemLike> topVisited = getTopVisitedPoems();
        List<PoemLike> topLiked = getTopLikedPoems();

        // 存储到 Redis 中
        redisTemplate.opsForValue().set("topVisited", topVisited);
        redisTemplate.opsForValue().set("topLiked", topLiked);

        // 获取详细信息并存储
        topVisited.forEach(poem -> {
            String detail = fetchPoemDetailFromMySQL(poem.getPoemId());
            redisTemplate.opsForHash().put("poemDetails", poem.getPoemId(), detail);
        });
        System.out.println("Redis 缓存已更新");
    }

    /**
     * 定时任务，每5分钟更新 Redis 缓存
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void scheduleCacheUpdate() {
        updateRedisCache();
    }
}
