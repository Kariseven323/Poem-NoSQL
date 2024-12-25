package com.sspu.nsrank.service;

import com.sspu.nsrank.model.AncientPoetry;
import com.sspu.nsrank.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RankService {

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // MySQL配置
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/poem?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "zsh123";

    // 核心功能：获取前50诗词信息并存入Redis
    public void saveTopPoemsToRedis() {
        log.info("开始获取诗词数据并保存到Redis");
        try {
            // 如果没有点赞数据，直接从MySQL获取前50首诗词
            List<AncientPoetry> poems = new ArrayList<>();
            
            try (Connection connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
                String sql = "SELECT * FROM ancient_poetry LIMIT 50";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
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
                            poems.add(poetry);
                        }
                    }
                }
            }

            log.info("成功获取到 {} 首诗词", poems.size());

            // 清空之前的数据
            redisTemplate.delete("top_poems");

            // 将诗词信息写入Redis
            for (AncientPoetry poetry : poems) {
                redisTemplate.opsForHash().put("top_poems", poetry.getId(), poetry);
            }

            log.info("成功将诗词数据保存到Redis");
        } catch (Exception e) {
            log.error("保存诗词信息到Redis失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存诗词信息到Redis失败: " + e.getMessage());
        }
    }
}
