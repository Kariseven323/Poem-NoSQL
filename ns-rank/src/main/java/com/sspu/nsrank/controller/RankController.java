package com.sspu.nsrank.controller;

import com.sspu.nsrank.entity.RankedPoetry;
import com.sspu.nsrank.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    // 获取诗词前100排名
    @GetMapping("/top100")
    public List<RankedPoetry> getTop100RankedPoems() {
        return rankService.getTop100RankedPoems();
    }

    // 更新诗词分数（管理员或内部调用）
    @PostMapping("/update/{poemId}")
    public void updatePoemScore(
            @PathVariable String poemId,
            @RequestParam double score) {
        rankService.updatePoemScore(poemId, score);
    }
}
