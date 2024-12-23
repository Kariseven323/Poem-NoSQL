package com.sspu.nslike.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * 诗词点赞实体类
 * 用于存储诗词的点赞信息
 * @author [your name]
 * @version 1.0
 * @since [date]
 */
@Data
@Document(collection = "poem_likes")
@CompoundIndex(name = "poemId_idx", def = "{'poemId': 1}", unique = true)
public class PoemLike {
    /**
     * 诗词ID
     */
    @Id
    private String poemId;

    /**
     * 点赞数
     */
    private int likeCount;

    /**
     * 访问量
     */
    private int visitCount = 0;

    /**
     * 点赞用户ID集合
     */
    private Set<String> likedUserIds = new HashSet<>();
}
