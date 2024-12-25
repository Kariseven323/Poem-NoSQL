package com.sspu.nsrank.service;

import com.sspu.nsrank.model.AncientPoetry;
import com.sspu.nsrank.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.sspu.nslike.model.PoemLike;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    @Autowired
    private PoemLikeRepository poemLikeRepository; // MongoDB的诗词点赞数据仓库

    @Autowired
    private RedisTemplate<String, Object> redisTemplate; // Redis操作类

    // MySQL配置
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "cdj123";

    // 核心功能：获取前50诗词信息并存入Redis
    public void saveTopPoemsToRedis() {
        try (Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            // 从MongoDB获取前50个poemId
            // Step 1: 从 MongoDB 获取前 50 个 poemId，使用自定义查询代替 findAll
            List<String> topPoemIds = poemLikeRepository.findTop50ByOrderByLikeCountAndVisitCount()
                    .stream()
                    .map(PoemLike::getPoemId)
                    .collect(Collectors.toList());

            // 根据poemId从MySQL获取完整古诗词信息
            for (String poemId : topPoemIds) {
                AncientPoetry poetry = fetchPoemFromMySQL(connection, poemId);
                if (poetry != null) {
                    // 将诗词信息写入Redis（使用Hash结构）
                    redisTemplate.opsForHash().put("top_poems", poetry.getId(), poetry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("保存诗词信息到Redis失败: " + e.getMessage());
        }
    }

    // 从MySQL获取诗词信息
    private AncientPoetry fetchPoemFromMySQL(Connection connection, String poemId) throws Exception {
        String sql = "SELECT * FROM ancient_poetry WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, poemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    AncientPoetry poetry = new AncientPoetry();
                    poetry.setId(resultSet.getString("id"));
                    poetry.setTitle(resultSet.getString("title"));
                    poetry.setDynasty(resultSet.getString("dynasty"));
                    poetry.setWriter(resultSet.getString("writer"));
                    poetry.setType(resultSet.getString("type"));
                    poetry.setContent(resultSet.getString("content"));
                    poetry.setRemark(resultSet.getString("remark"));
                    poetry.setTranslation(resultSet.getString("translation"));
                    poetry.setShangxi(resultSet.getString("shangxi"));
                    poetry.setAudioUrl(resultSet.getString("audioUrl"));
                    return poetry;
                }
            }
        }
        return null; // 如果没有找到返回null
    }
}
