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
