package com.sspu.nuser.service;

import com.sspu.nuser.entity.User;
import com.sspu.nuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        // 检查用户是否存在
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();

            // 更新用户的各个字段
            if (user.getUsername() != null) updatedUser.setUsername(user.getUsername());
            if (user.getPhone() != null) updatedUser.setPhone(user.getPhone());
            if (user.getPassword() != null) {
                // 加密存储密码
                updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (user.getGender() != null) updatedUser.setGender(user.getGender());
            if (user.getSignature() != null) updatedUser.setSignature(user.getSignature());

            // 保存更新后的用户
            return userRepository.save(updatedUser);
        } else {
            // 用户不存在时，抛出异常
            throw new RuntimeException("User with ID " + user.getId() + " not found");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
