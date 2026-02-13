package com.booktrad.ai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.booktrad.ai.service.QwenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 通义千问AI服务实现类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@Service
public class QwenServiceImpl implements QwenService {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;
    
    @Value("${ai.dashscope.model}")
    private String model;
    
    @Value("${ai.dashscope.timeout:30}")
    private Integer timeout;

    @Override
    public String generateBookDescription(String title, String author, String category, Integer condition) {
        try {
            // 构建提示词
            String conditionText = getConditionText(condition);
            String prompt = String.format(
                "请为以下二手书籍生成一段吸引人的描述（100-150字）：\n" +
                "书名：%s\n" +
                "作者：%s\n" +
                "分类：%s\n" +
                "成色：%s\n" +
                "要求：\n" +
                "1. 简要介绍书籍内容和特点\n" +
                "2. 说明书籍成色\n" +
                "3. 适合什么样的读者\n" +
                "4. 语气友好、真诚",
                title, author, category, conditionText
            );

            log.info("调用通义千问生成书籍描述，书名: {}", title);
            
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是一个专业的二手书交易平台的文案助手，擅长撰写吸引人的书籍描述。")
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                String description = result.getOutput().getChoices().get(0).getMessage().getContent();
                log.info("成功生成书籍描述");
                return description;
            }
            
            log.warn("通义千问返回结果为空");
            return null;
            
        } catch (Exception e) {
            log.error("调用通义千问API失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String parseQueryIntent(String query) {
        try {
            // 构建提示词
            String prompt = String.format(
                "请将以下自然语言查询转换为结构化的JSON格式查询条件：\n" +
                "用户查询：%s\n\n" +
                "请返回JSON格式，包含以下字段（如果用户没有提到某个条件，则不包含该字段）：\n" +
                "- keyword: 关键词（书名、作者等）\n" +
                "- category: 分类（教材、小说、技术、考试等）\n" +
                "- minPrice: 最低价格（数字）\n" +
                "- maxPrice: 最高价格（数字）\n" +
                "- condition: 成色（1-全新 2-九成新 3-八成新 4-明显使用痕迹）\n" +
                "- tradeType: 交易方式（自取、邮寄）\n\n" +
                "只返回JSON，不要其他说明文字。",
                query
            );

            log.info("调用通义千问解析查询意图: {}", query);
            
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是一个专业的查询意图解析助手，擅长将自然语言转换为结构化查询条件。")
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                String jsonResult = result.getOutput().getChoices().get(0).getMessage().getContent();
                log.info("成功解析查询意图");
                return jsonResult;
            }
            
            log.warn("通义千问返回结果为空");
            return null;
            
        } catch (Exception e) {
            log.error("调用通义千问API失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String callAI(String systemPrompt, String userPrompt) {
        try {
            log.info("调用通义千问AI");
            
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemPrompt)
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userPrompt)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                String response = result.getOutput().getChoices().get(0).getMessage().getContent();
                log.info("AI调用成功");
                return response;
            }
            
            log.warn("通义千问返回结果为空");
            return null;
            
        } catch (Exception e) {
            log.error("调用通义千问API失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取成色文本描述
     */
    private String getConditionText(Integer condition) {
        if (condition == null) {
            return "未知";
        }
        switch (condition) {
            case 1: return "全新";
            case 2: return "九成新";
            case 3: return "八成新";
            case 4: return "明显使用痕迹";
            default: return "未知";
        }
    }

    @Override
    public String chatWithContext(String message, java.util.List<com.booktrad.ai.entity.AiChatMessage> historyMessages) {
        try {
            log.info("调用通义千问进行对话，历史消息数: {}", historyMessages.size());
            
            Generation gen = new Generation();
            java.util.List<Message> messages = new java.util.ArrayList<>();
            
            // 添加系统提示词
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是BookTrad二手书交易平台的AI智能助手。你的职责是：\n" +
                            "1. 帮助用户了解平台功能和使用方法\n" +
                            "2. 解答关于书籍交易的问题\n" +
                            "3. 提供书籍推荐和搜索建议\n" +
                            "4. 协助处理订单、支付等相关问题\n" +
                            "请保持友好、专业的态度，用简洁明了的语言回答用户问题。")
                    .build();
            messages.add(systemMsg);
            
            // 添加历史消息（保持对话上下文）
            for (com.booktrad.ai.entity.AiChatMessage historyMsg : historyMessages) {
                String role = "user".equals(historyMsg.getRole()) ? 
                        Role.USER.getValue() : Role.ASSISTANT.getValue();
                Message msg = Message.builder()
                        .role(role)
                        .content(historyMsg.getContent())
                        .build();
                messages.add(msg);
            }
            
            // 添加当前用户消息
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(message)
                    .build();
            messages.add(userMsg);

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                String reply = result.getOutput().getChoices().get(0).getMessage().getContent();
                log.info("AI对话成功");
                return reply;
            }
            
            log.warn("通义千问返回结果为空");
            return "抱歉，我现在无法回答您的问题，请稍后再试。";
            
        } catch (Exception e) {
            log.error("调用通义千问API失败: {}", e.getMessage(), e);
            return "抱歉，服务暂时不可用，请稍后再试。";
        }
    }
}
