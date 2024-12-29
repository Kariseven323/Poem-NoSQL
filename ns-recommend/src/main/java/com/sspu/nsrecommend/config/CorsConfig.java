/**
 * @file CorsConfig.java
 * @description 跨域配置类
 * 功能：
 * 1. 配置跨域资源共享(CORS)策略
 * 2. 允许前端跨域访问后端API
 * 3. 设置允许的请求方法和头信息
 * 4. 配置跨域请求的缓存时间
 * 5. 支持 Cookie 跨域传输
 */

package com.sspu.nsrecommend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许cookies跨域
        config.setAllowCredentials(true);
        
        // 允许向该服务器提交请求的URI，*表示全部允许
        config.addAllowedOriginPattern("*");
        
        // 允许访问的头信息,*表示全部
        config.addAllowedHeader("*");
        
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(18000L);
        
        // 允许提交请求的方法，*表示全部允许
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 