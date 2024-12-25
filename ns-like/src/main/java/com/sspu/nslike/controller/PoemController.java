package com.sspu.nslike.controller;

import com.sspu.nslike.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class PoemController {
    @Autowired
    private PoemService poemService;

    // 初始化数据
    @PostMapping("/initialize")
    public void initializeLikes() {
        poemService.initializeLikesAsync();
    }

    // 点赞/取消点赞
    @PostMapping("/{poemId}/toggle")
    public String toggleLike(@PathVariable String poemId, @RequestParam String userId) {
        boolean result = poemService.toggleLike(poemId, userId);
        return result ? "操作成功" : "操作失败";
    }
}
