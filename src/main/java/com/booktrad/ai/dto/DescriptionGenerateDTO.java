package com.booktrad.ai.dto;

import lombok.Data;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI生成描述DTO
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class DescriptionGenerateDTO {
    
    /**
     * 书名
     */
    private String title;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 成色：1-全新 2-九成新 3-八成新 4-明显使用痕迹
     */
    private Integer condition;
}
