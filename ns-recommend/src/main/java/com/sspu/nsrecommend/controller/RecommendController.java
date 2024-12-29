package com.sspu.nsrecommend.controller;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nsrecommend.service.RecommendService;
import com.sspu.nsrecommend.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private UserBehaviorService userBehaviorService;

    // 记录用户行为
    @PostMapping("/{userId}/record")
    public ResponseEntity<?> recordBehavior(
            @PathVariable("userId") String userId,
            @RequestParam("poemId") String poemId) {
        try {
            userBehaviorService.recordBehavior(userId, poemId);
            return ResponseEntity.ok("行为记录成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("记录失败：" + e.getMessage());
        }
    }

    // 获取基于用户行为的推荐
    @GetMapping("/{userId}")
    public ResponseEntity<List<AncientPoetry>> getRecommendations(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(recommendService.recommendPoems(userId));
    }

    // 获取基于协同过滤的推荐
    @GetMapping("/{userId}/collaborative")
    public ResponseEntity<List<AncientPoetry>> getCollaborativeRecommendations(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(recommendService.collaborativeFilteringRecommend(userId));
    }
}
