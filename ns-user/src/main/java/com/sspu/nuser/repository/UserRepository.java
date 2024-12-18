/**
 * @file UserRepository.java
 * @description 用户数据访问接口
 * 功能：
 * 1. 提供用户数据的访问方法
 * 2. 继承 JPA 基础操作接口
 * 3. 定义自定义查询方法
 * 4. 实现用户信息的增删改查
 * 5. 提供用户名和手机号查重功能
 */

package com.sspu.nuser.repository;

import com.sspu.nuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
}
