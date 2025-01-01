package com.sspu.nsrank.entity;

import lombok.Data;

@Data
public class RankedPoetry {
    private String id;         // 诗词 ID
    private String title;      // 诗词标题
    private String writer;     // 作者
    private int rank;          // 排名
    private int score;         // 根据点赞数或评论数计算的分数
}
