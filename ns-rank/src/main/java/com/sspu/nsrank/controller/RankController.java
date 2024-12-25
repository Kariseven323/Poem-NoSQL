package com.sspu.nsrank.controller;

import com.sspu.nsrank.service.RankService;
import com.sspu.nsrank.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/rank")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true", maxAge = 3600)
public class RankController {

    @Autowired
    private RankService rankService;

    @Autowired
    private RedisService redisService;

    // 手动刷新Redis中的诗词排行数据
    @GetMapping("/refresh")
    public String refreshTopPoems() {
        log.info("开始刷新Redis中的诗词排行数据");
        try {
            rankService.saveTopPoemsToRedis();
            log.info("刷新Redis数据成功");
            return "Top poems saved to Redis successfully!";
        } catch (Exception e) {
            log.error("刷新Redis数据失败: {}", e.getMessage());
            return "Failed to refresh Redis data: " + e.getMessage();
        }
    }

    // 获取Redis中的诗词排行数据
    @GetMapping("/top")
    public Map<Object, Object> getTopPoems() {
        log.info("开始获取Redis中的诗词排行数据");
        try {
            Map<Object, Object> result = redisService.getTopPoemsFromRedis();
            log.info("成功获取到 {} 条诗词数据", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取Redis数据失败: {}", e.getMessage());
            throw e;
        }
    }
}
