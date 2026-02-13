package com.booktrad.ai.dto;

import lombok.Data;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI查询DTO - 用于自然语言查询书籍
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class AIQueryDTO {
    
    /**
     * 用户的自然语言查询
     * 例如："我想找一本关于Java的书，价格在50元以下"
     */
    private String query;
    
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
