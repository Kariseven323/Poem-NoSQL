package com.sspu.nsrank.controller;

import com.sspu.nsrank.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    /**
     * 手动触发 Redis 缓存更新
     * @return 成功消息
     */
    @GetMapping("/update-cache")
    public String updateCache() {
        rankService.updateRedisCache();
        return "Redis 缓存更新成功！";
    }

    /**
     * 获取访问量前50的诗词（从 Redis 读取）
     * @return JSON 格式的诗词列表
     */
    @GetMapping("/top-visited")
    public Object getTopVisitedPoems() {
        return rankService.getCachedTopVisitedPoems();
    }

    /**
     * 获取点赞量前50的诗词（从 Redis 读取）
     * @return JSON 格式的诗词列表
     */
    @GetMapping("/top-liked")
    public Object getTopLikedPoems() {
        return rankService.getCachedTopLikedPoems();
    }
}
