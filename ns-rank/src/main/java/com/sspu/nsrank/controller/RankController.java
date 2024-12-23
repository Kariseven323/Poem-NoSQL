package com.sspu.nsrank.controller;

import com.sspu.nsrank.service.RankService;
import com.sspu.nsrank.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    @Autowired
    private RedisService redisService;

    // 手动刷新Redis中的诗词排行数据
    @GetMapping("/refresh")
    public String refreshTopPoems() {
        rankService.saveTopPoemsToRedis();
        return "Top poems saved to Redis successfully!";
    }

    // 获取Redis中的诗词排行数据
    @GetMapping("/top")
    public Map<Object, Object> getTopPoems() {
        return redisService.getTopPoemsFromRedis();
    }
}
