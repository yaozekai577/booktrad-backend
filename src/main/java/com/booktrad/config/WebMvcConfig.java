package com.booktrad.config;

import com.booktrad.interceptor.JwtLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  WebMvc配置类，用于配置Spring Boot的WebMvc相关功能，包括跨域配置和拦截器配置 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtLoginInterceptor jwtLoginInterceptor;

    /**
     * 配置CORS跨域
     * 允许前端浏览器访问后端接口
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOriginPatterns("*") // 允许所有来源（生产环境建议指定具体域名）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带凭证（如cookie）
                .maxAge(3600); // 预检请求缓存时间
    }

    /**
     * 注册拦截器
     * 配置登录和注册接口放行，其他接口需要token校验
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT登录拦截器
        registry.addInterceptor(jwtLoginInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录和注册接口，这两个接口不拦截
                .excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}