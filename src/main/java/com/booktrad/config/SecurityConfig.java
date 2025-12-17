package com.booktrad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置类
 * 用于配置Spring Security相关功能，禁用默认的表单登录和会话管理，启用JWT认证
 *
 * @author 
 * @since 2025-12-17
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置BCryptPasswordEncoder
     * 用于密码加密和验证
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置SecurityFilterChain
     * 禁用默认的表单登录、会话管理和CSRF保护，使用JWT认证
     * @param http HttpSecurity对象
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（JWT认证不需要CSRF）
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用表单登录（使用自定义的JWT登录接口）
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用HTTP基本认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 禁用会话管理（JWT是无状态的）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 允许所有请求（我们使用自定义的JWT拦截器来进行认证）
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
