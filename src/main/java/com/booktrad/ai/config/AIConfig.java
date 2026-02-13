package com.booktrad.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI功能配置类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    
    /**
     * 通义千问配置
     */
    private DashScope dashscope = new DashScope();
    
    /**
     * Google Books配置
     */
    private GoogleBooks googleBooks = new GoogleBooks();
    
    @Data
    public static class DashScope {
        /**
         * API密钥
         */
        private String apiKey;
        
        /**
         * 模型名称
         */
        private String model = "qwen-turbo";
        
        /**
         * 超时时间（秒）
         */
        private Integer timeout = 30;
    }
    
    @Data
    public static class GoogleBooks {
        /**
         * API地址
         */
        private String apiUrl = "https://www.googleapis.com/books/v1/volumes";
        
        /**
         * API密钥（可选）
         */
        private String apiKey;
    }
}
