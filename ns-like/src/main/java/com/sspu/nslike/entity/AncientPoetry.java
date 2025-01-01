package com.sspu.nslike.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Table(name = "ancient_poetry") // 对应 MySQL 表
@Data
@Document(indexName = "ancientpoetry") // 对应 Elasticsearch 索引（必须小写）
public class AncientPoetry {

    @Id
    @Column(length = 50, nullable = false) // MySQL 中的主键
    @org.springframework.data.annotation.Id // Elasticsearch 的主键
    private String id;

    @Column(length = 255, nullable = false) // MySQL 的标题字段
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart") // Elasticsearch 的全文索引
    private String title;

    @Column(length = 50) // MySQL 的朝代字段
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String dynasty;

    @Column(length = 100) // MySQL 的作者字段
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String writer;

    @Lob // 对应 MySQL 中的长文本类型
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String type;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT") // MySQL 中的正文
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Lob
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String remark;

    @Lob
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String translation;

    @Lob
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String shangxi;

    @Column(length = 2083) // 音频链接
    @Field(type = FieldType.Keyword) // Elasticsearch 的精确匹配字段
    private String audioUrl;

    // Lombok 的 @Data 自动生成 Getters 和 Setters
}
