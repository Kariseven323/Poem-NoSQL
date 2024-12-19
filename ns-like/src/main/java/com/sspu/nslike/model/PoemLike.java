package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "poem_likes")
@CompoundIndex(name = "poemId_idx", def = "{'poemId': 1}", unique = true)
public class PoemLike {
    @Id
    private String id;
    private String poemId; // 诗词ID
    private int likeCount; // 点赞数量
    private Set<String> likedUserIds = new HashSet<>(); // 保存点赞过该诗词的用户ID
    private int visitCount = 0; // 访问量，默认值为0
}
