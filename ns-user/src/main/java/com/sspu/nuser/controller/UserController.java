/**
 * @file UserController.java
 * @description 用户控制器
 * 功能：
 * 1. 处理用户相关的HTTP请求
 * 2. 提供用户注册接口
 * 3. 提供用户登录接口
 * 4. 提供用户信息查询接口
 * 5. 提供用户信息更新接口
 * 6. 提供用户删除接口
 */

package com.sspu.nuser.controller;

import com.sspu.nuser.entity.User;
import com.sspu.nuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Optional<User> login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return userService.login(username, password);
    }

    @GetMapping("/get/{id}")
    public Optional<User> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        // 设置用户的 ID 确保与路径参数一致
        user.setId(id);
        return userService.updateUser(user);
    }

    @DeleteMapping("/del/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
