package com.sspu.nsrank.controller;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nsrank.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    // 获取前 100 的诗词
    @GetMapping("/top100")
    public ResponseEntity<List<AncientPoetry>> getTop100() {
        return ResponseEntity.ok(rankService.getTop100Poems());
    }
}
