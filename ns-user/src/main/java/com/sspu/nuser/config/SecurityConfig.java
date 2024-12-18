/**
 * @file SecurityConfig.java
 * @description Spring Security 安全配置类
 * 功能：
 * 1. 配置 Spring Security 安全框架
 * 2. 提供密码加密器
 * 3. 配置接口访问权限
 * 4. 禁用 CSRF 保护
 * 5. 配置认证规则
 */

package com.sspu.nuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 替代原来的 .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").permitAll() // 放行用户模块相关接口
                        .anyRequest().authenticated() // 其他接口需要认证
                );
        return http.build();
    }
}
