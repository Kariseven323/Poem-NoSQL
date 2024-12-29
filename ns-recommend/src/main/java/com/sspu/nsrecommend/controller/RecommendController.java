package com.sspu.nsrecommend.controller;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nsrecommend.service.RecommendService;
import com.sspu.nsrecommend.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private UserBehaviorService userBehaviorService;

    // 定义 Logger
    private static final Logger log = LoggerFactory.getLogger(RecommendController.class);
    // 记录用户阅读行为，基于 poemId
    @PostMapping("/{userId}/record")
    public ResponseEntity<?> recordBehavior(
            @PathVariable("userId") String userId,
            @RequestParam("poemId") String poemId) {
        try {
            userBehaviorService.recordBehavior(userId, poemId);
            return ResponseEntity.ok("行为记录成功");
        } catch (IllegalArgumentException e) {
            log.error("记录行为失败：参数错误，poemId={} userId={}", poemId, userId, e);
            return ResponseEntity.badRequest().body("记录失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("记录行为失败：内部错误，poemId={} userId={}", poemId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器错误：" + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AncientPoetry>> getRecommendations(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(recommendService.recommendPoems(userId));
    }
}
