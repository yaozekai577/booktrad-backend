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
    
    /**
     * 调用AI获取通用响应
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @return AI响应内容
     */
    String callAI(String systemPrompt, String userPrompt);
    
    /**
     * 带上下文的对话
     * @param message 用户消息
     * @param historyMessages 历史消息列表
     * @return AI回复
     */
    String chatWithContext(String message, java.util.List<com.booktrad.ai.entity.AiChatMessage> historyMessages);
    
    /**
     * 流式对话（带上下文）
     * @param message 用户消息
     * @param historyMessages 历史消息列表
     * @param callback 流式回调接口
     */
    void chatWithContextStream(String message, java.util.List<com.booktrad.ai.entity.AiChatMessage> historyMessages, StreamCallback callback);
    
    /**
     * 流式输出回调接口
     */
    interface StreamCallback {
        /**
         * 接收流式输出的文本片段
         * @param text 文本片段
         */
        void onNext(String text);
        
        /**
         * 流式输出完成
         * @param fullText 完整文本
         */
        void onComplete(String fullText);
        
        /**
         * 流式输出出错
         * @param error 错误信息
         */
        void onError(String error);
    }
}
