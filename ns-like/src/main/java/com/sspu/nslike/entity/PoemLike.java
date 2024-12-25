package com.sspu.nslike.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "poem_likes")
@Data
public class PoemLike {
    @Id
    private String id;
    private String poemId;
    private int likeCount;
    private Set<String> userIds; // 记录点赞用户的 ID

    public PoemLike() {
        this.likeCount = 0;
        this.userIds = new HashSet<>();
    }

    // Getters and Setters
}
