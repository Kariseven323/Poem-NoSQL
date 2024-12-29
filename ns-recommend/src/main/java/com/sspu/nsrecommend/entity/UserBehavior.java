package com.sspu.nsrecommend.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "user_behaviors")
public class UserBehavior {
    @Id
    private String userId; // 用户ID
    private List<Behavior> behaviors = new ArrayList<>();  // 用户的行为数据

    @Data
    public static class Behavior {
        private String type; // 诗词类型
        private int viewCount; // 浏览次数
    }
}
