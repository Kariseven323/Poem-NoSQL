/**
 * @file User.java
 * @description 用户实体类
 * 功能：
 * 1. 定义用户数据模型
 * 2. 映射数据库表结构
 * 3. 提供用户属性的访问方法
 * 4. 使用 JPA 注解配置实体关系
 * 5. 使用 Lombok 简化代码
 */

package com.sspu.nuser.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String phone;

    private String gender;
    private LocalDateTime registerDate = LocalDateTime.now();
    private LocalDateTime updateDate = LocalDateTime.now();
    private String signature;

    @Column(nullable = false)
    private String password;
}
