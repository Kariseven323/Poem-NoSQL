package com.sspu.nslike.controller;

import com.sspu.nslike.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class PoemController {

    @Autowired
    private PoemService poemService;

    // 初始化数据
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeLikes() {
        try {
            poemService.initializeLikesAsync();
            return ResponseEntity.ok("初始化成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("初始化失败：" + e.getMessage());
        }
    }

    // 点赞/取消点赞
    @PostMapping("/{poemId}/toggle")
    public ResponseEntity<String> toggleLike(
            @PathVariable("poemId") String poemId,
            @RequestParam("userId") String userId) {
        try {
            boolean result = poemService.toggleLike(poemId, userId);
            return result
                    ? ResponseEntity.ok("操作成功")
                    : ResponseEntity.badRequest().body("操作失败：点赞或取消点赞操作未完成");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("操作失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }
}
