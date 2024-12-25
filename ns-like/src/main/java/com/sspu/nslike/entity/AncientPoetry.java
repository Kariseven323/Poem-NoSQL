package com.sspu.nslike.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ancient_poetry")
@Data
public class AncientPoetry {

    @Id
    @Column(length = 50, nullable = false) // 对应 MySQL 中的 VARCHAR(50)
    private String id;

    @Column(length = 255, nullable = false) // 对应 MySQL 中的 VARCHAR(255)
    private String title;

    @Column(length = 50) // 对应 MySQL 中的 VARCHAR(50)
    private String dynasty;

    @Column(length = 100) // 对应 MySQL 中的 VARCHAR(100)
    private String writer;

    @Lob // 对应 MySQL 中的 TEXT
    private String type;

    @Lob // 对应 MySQL 中的 TEXT
    @Column(nullable = false)
    private String content;

    @Lob // 对应 MySQL 中的 TEXT
    private String remark;

    @Lob // 对应 MySQL 中的 TEXT
    private String translation;

    @Lob // 对应 MySQL 中的 TEXT
    private String shangxi;

    @Column(length = 2083) // 对应 MySQL 中的 VARCHAR(2083)
    private String audioUrl;

    // Getters and Setters 由 Lombok 的 @Data 自动生成
}
