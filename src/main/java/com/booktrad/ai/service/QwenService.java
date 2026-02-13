package com.booktrad.ai.service;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 通义千问AI服务接口
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface QwenService {
    
    /**
     * 生成书籍描述
     * @param title 书名
     * @param author 作者
     * @param category 分类
     * @param condition 成色
     * @return 生成的描述
     */
    String generateBookDescription(String title, String author, String category, Integer condition);
    
    /**
     * 解析自然语言查询为结构化查询条件
     * @param query 用户的自然语言查询
     * @return JSON格式的查询条件
     */
    String parseQueryIntent(String query);
}
