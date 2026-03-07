package com.booktrad.ai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.booktrad.ai.service.QwenService;
import io.reactivex.Flowable;
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
                "请为以下二手书籍生成一段吸引人的描述（100-150字），不用引导用户回复：\n" +
                "书名：%s\n" +
                "作者：%s\n" +
                "分类：%s\n" +
                "成色：%s\n" +
                "要求：\n" +
                "1. 简要介绍书籍的真实内容和特点，比如《活着》讲两个兄弟艰苦的成长生活等等\n" +
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
                    .content("你是BookTrad校园二手书交易平台的AI智能助手。你的职责是帮助用户买书、卖书和解答平台使用问题。\n\n" +
                            "【平台核心功能指南】\n" +
                            "1. **发布书籍（卖书）**：\n" +
                            "   - 入口：点击首页右上角的绿色“发布书籍”按钮。\n" +
                            "   - 流程：登录后，在弹出的窗口中填写相关书籍信息，包括：**书名、价格、分类、成色、书籍描述、瑕疵说明以及期望交易地点**等，并上传至少一张书籍实拍图，最后点击“立即发布”。\n" +
                            "   - 注意：必须登录才能发布。\n\n" +
                            "2. **购买书籍（买书）**：\n" +
                            "   - 浏览：在首页浏览最新上架书籍，或使用顶部搜索框搜索书名/ISBN。\n" +
                            "   - 筛选：可以使用分类标签（大学教材、文学小说等）或排序功能（价格、时间）筛选。\n" +
                            "   - 下单：点击书籍封面进入详情页，点击“立即购买”；或在首页卡片上点击“交易”。确认订单信息后提交。\n\n" +
                            "3. **联系卖家**：\n" +
                            "   - 在书籍详情页或首页卡片上点击“联系卖家”按钮，可进入聊天窗口与卖家直接沟通。\n\n" +
                            "4. **个人中心**：\n" +
                            "   - 点击右上角用户名，可以查看“我的订单”、“我的评价”或“修改密码”。\n\n" +
                            "【回答原则】\n" +
                            "- 当用户问“怎么卖书”、“如何发布”时，请引导他们点击右上角“发布书籍”按钮，并提醒他们需要填写书名、价格、描述、瑕疵及交易地点等信息。\n" +
                            "- 当用户问“怎么买书”时，引导他们搜索或浏览，并说明下单流程。\n" +
                            "- 当用户询问具体技术书籍（如Java、Python）时，先简要介绍该技术，然后建议用户在平台搜索框输入相关关键词查找。\n" +
                            "- 保持语气热情、亲切，像学长学姐一样。\n" +
                            "- 当用户只是打招呼（如“你好”、“在吗”），请简短回复，例如：“你好呀！我是你的二手书小助手，想找书还是卖书呢？”\n" +
                            "- 不要编造平台不存在的功能（如“购物车”、“会员”等，目前平台主要是直接交易）。")
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

            // 增加流式输出或者调整超时设置（虽然DashScope SDK默认超时较短，这里可以通过GenerationParam尝试优化）
            // 注意：DashScope Java SDK 2.14.0+ 支持流式调用，但这里为了兼容现有同步接口，我们主要依赖后端超时配置
            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    // 启用增量输出模式（流式），虽然当前是同步调用，但这有助于模型更快开始生成
                    .enableSearch(true) 
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
            return "抱歉,服务暂时不可用,请稍后再试。";
        }
    }

    @Override
    public void chatWithContextStream(String message, java.util.List<com.booktrad.ai.entity.AiChatMessage> historyMessages, QwenService.StreamCallback callback) {
        try {
            log.info("调用通义千问进行流式对话，历史消息数: {}", historyMessages.size());
            
            Generation gen = new Generation();
            java.util.List<Message> messages = new java.util.ArrayList<>();
            
            // 添加系统提示词
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content("你是BookTrad校园二手书交易平台的AI智能助手。你的职责是帮助用户买书、卖书和解答平台使用问题，以及书籍推荐；当用户询问平台使用的相关问题时，你需要根据【平台核心功能指南】和【回答原则】来回答。\n\n" +
                            "【平台核心功能指南】\n" +
                            "1. **发布书籍（卖书）**：\n" +
                            "   - 入口：点击首页右上角的绿色“发布书籍”按钮。\n" +
                            "   - 流程：登录后，在弹出的窗口中填写相关书籍信息，包括：**书名、价格、分类、成色、书籍描述、瑕疵说明以及期望交易地点**等，并上传至少一张书籍实拍图，最后点击“立即发布”。\n" +
                            "   - 注意：必须登录才能发布。\n\n" +
                            "2. **购买书籍（买书）**：\n" +
                            "   - 浏览：在首页浏览最新上架书籍，或使用顶部搜索框搜索书名/ISBN。\n" +
                            "   - 筛选：可以使用分类标签（大学教材、文学小说等）或排序功能（价格、时间）筛选。\n" +
                            "   - 下单：点击书籍封面进入详情页，可先点击联系卖家按钮，沟通价格以及交易地点，合适之后点击“交易”。确认订单信息后提交。\n\n" +
                            "3. **联系卖家**：\n" +
                            "   - 在书籍详情页或首页卡片上点击“联系卖家”按钮，可进入聊天窗口与卖家直接沟通。\n\n" +
                            "4. **个人中心**：\n" +
                            "   - 点击右上角用户名，可以查看“我的订单”、“我的评价”或“修改密码”。\n\n" +
                            "【回答原则】\n" +
                            "- 当用户问“怎么卖书”、“如何发布”时，请引导他们点击右上角“发布书籍”按钮，并提醒他们需要填写书名、价格、描述、瑕疵及交易地点等信息。\n" +
                            "- 当用户问“怎么买书”时，引导他们搜索或浏览，并说明下单流程。\n" +
                            "- 当用户询问具体技术书籍（如Java、Python）时，先简要介绍该技术，然后建议用户在平台搜索框输入相关关键词查找。\n" +
                            "- 保持语气热情、亲切，像学长学姐一样。\n" +
                            "- 当用户只是打招呼（如“你好”、“在吗”），请简短回复，例如：“你好呀！我是你的二手书小助手，想找书还是卖书呢？”\n" +
                            "- 你可以推荐相关专业或者课程所需要的书籍，但是不要编造平台不存在的书籍\n" +
                            "- 不要编造平台不存在的功能（如“购物车”、“会员”等，目前平台主要是直接交易）。")
                    .build();
            messages.add(systemMsg);
            
            // 添加历史消息
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

            // 配置流式输出参数
            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(model)
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .incrementalOutput(true) // 启用增量输出
                    .build();

            // 调用流式API
            Flowable<GenerationResult> resultFlowable = gen.streamCall(param);
            
            StringBuilder fullText = new StringBuilder();
            
            // 订阅流式结果
            resultFlowable.blockingForEach(result -> {
                if (result != null && result.getOutput() != null && 
                    result.getOutput().getChoices() != null && 
                    !result.getOutput().getChoices().isEmpty()) {
                    String text = result.getOutput().getChoices().get(0).getMessage().getContent();
                    fullText.append(text);
                    // 回调每个文本片段
                    callback.onNext(text);
                }
            });
            
            // 完成回调
            callback.onComplete(fullText.toString());
            log.info("AI流式对话完成");
            
        } catch (Exception e) {
            log.error("调用通义千问流式API失败: {}", e.getMessage(), e);
            callback.onError("抱歉，服务暂时不可用，请稍后再试。");
        }
    }
}
