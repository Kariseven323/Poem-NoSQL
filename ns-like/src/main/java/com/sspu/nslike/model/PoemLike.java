package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "poem_likes")
public class PoemLike {
    @Id
    private String id;
    private String poemId; // 诗词ID
    private String userId; // 点赞用户ID
}
