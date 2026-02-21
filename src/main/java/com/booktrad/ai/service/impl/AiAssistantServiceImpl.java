package com.booktrad.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.ai.entity.AiChatMessage;
import com.booktrad.ai.entity.AiChatSession;
import com.booktrad.ai.mapper.AiChatMessageMapper;
import com.booktrad.ai.mapper.AiChatSessionMapper;
import com.booktrad.ai.service.AIQueryService;
import com.booktrad.ai.service.AiAssistantService;
import com.booktrad.ai.service.QwenService;
import com.booktrad.ai.vo.AiChatMessageVO;
import com.booktrad.ai.vo.AiChatResponseVO;
import com.booktrad.ai.vo.AiChatSessionVO;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI智能助手服务实现类
 */
@Slf4j
@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Autowired
    private AiChatSessionMapper sessionMapper;
    
    @Autowired
    private AiChatMessageMapper messageMapper;
    
    @Autowired
    private QwenService qwenService;
    
    @Autowired
    private AIQueryService aiQueryService;

    @Override
    @Transactional
    public AiChatResponseVO chat(Long sessionId, String message) {
        Long userId = UserContext.getUserId();
        
        // 1. 如果没有会话ID，创建新会话
        if (sessionId == null) {
            AiChatSession session = new AiChatSession();
            session.setUserId(userId);
            // session.setTitle("AI助手对话");
            session.setMessageCount(0);
            sessionMapper.insert(session);
            sessionId = session.getId();
            log.info("创建新的AI对话会话，sessionId: {}", sessionId);
        } else {
            // 验证会话是否属于当前用户
            AiChatSession session = sessionMapper.selectById(sessionId);
            if (session == null || !session.getUserId().equals(userId)) {
                throw new RuntimeException("会话不存在或无权访问");
            }
        }
        
        // 2. 保存用户消息
        AiChatMessage userMessage = new AiChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRole("user");
        userMessage.setContent(message);
        messageMapper.insert(userMessage);
        
        // 3. 检查是否是书籍查询请求
        String aiReply;
        List<BookPageVO> bookResults = null;
        
        if (isBookQueryIntent(message)) {
            // 这是书籍查询请求,调用AI查询服务
            log.info("检测到书籍查询意图: {}", message);
            try {
                IPage<BookPageVO> bookPage = aiQueryService.queryBooksByNaturalLanguage(message, 1, 5);
                if (bookPage != null && bookPage.getRecords() != null && !bookPage.getRecords().isEmpty()) {
                    bookResults = bookPage.getRecords();
                    // 生成包含书籍信息的回复
                    aiReply = generateBookQueryResponse(message, bookResults);
                } else {
                    aiReply = "抱歉,没有找到符合您要求的书籍。您可以尝试:\n" +
                             "1. 使用更具体的关键词\n" +
                             "2. 调整价格范围\n" +
                             "3. 浏览其他分类";
                }
            } catch (Exception e) {
                log.error("书籍查询失败: {}", e.getMessage(), e);
                aiReply = "抱歉,查询书籍时出现了问题,请稍后再试。";
            }
        } else {
            // 普通对话,获取历史消息
            List<AiChatMessage> historyMessages = getRecentMessages(sessionId, 10);
            // 调用通义千问获取回复
            aiReply = qwenService.chatWithContext(message, historyMessages);
        }
        
        // 4. 保存AI回复
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(aiReply);
        messageMapper.insert(assistantMessage);
        
        // 5. 更新会话信息
        AiChatSession session = sessionMapper.selectById(sessionId);
        session.setLastMessage(message.length() > 100 ? message.substring(0, 100) + "..." : message);
        session.setMessageCount(session.getMessageCount() + 2);
        
        // 自动生成会话标题（第一次对话时）
        if (session.getMessageCount() <= 2 && (session.getTitle() == null || "AI助手对话".equals(session.getTitle()) || "新对话".equals(session.getTitle()))) {
            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
            session.setTitle(title);
            sessionMapper.updateById(session);
        } else {
             sessionMapper.updateById(session);
        }
        
        // 6. 构造返回结果
        AiChatResponseVO response = new AiChatResponseVO();
        response.setSessionId(sessionId);
        response.setReply(aiReply);
        response.setUserMessage(convertToVO(userMessage));
        response.setAssistantMessage(convertToVO(assistantMessage));
        response.setBookResults(bookResults); // 添加书籍查询结果
        
        return response;
    }
    
    /**
     * 判断用户消息是否是书籍查询意图
     */
    /**
     * 判断是否是书籍查询意图
     * 使用AI来智能判断用户意图
     */
    private boolean isBookQueryIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        
        // 1. 明确的书籍查询关键词（高优先级）
        String[] explicitBookKeywords = {
            "找书", "推荐书", "买书", "卖书", "书籍推荐",
            "有什么书", "哪些书", "什么书", "书单",
            "想买", "想要", "需要", "寻找", "搜索",
            "有没有", "找一本", "哪里有"
        };
        
        for (String keyword : explicitBookKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }
        
        // 2. 排除明确的知识问答关键词（高优先级）
        String[] knowledgeKeywords = {
            "是什么", "什么是", "怎么", "如何", "为什么",
            "介绍", "解释", "讲解", "原理", "概念",
            "学习", "教程", "入门", "基础", "知识"
        };
        
        for (String keyword : knowledgeKeywords) {
            if (lowerMessage.contains(keyword)) {
                return false; // 明确是知识问答，不是书籍查询
            }
        }
        
        // 3. 包含价格、成色等交易相关词汇（中优先级）
        String[] tradeKeywords = {
            "便宜", "价格", "多少钱", "成色", "新旧",
            "九成新", "八成新", "全新", "二手"
        };
        
        for (String keyword : tradeKeywords) {
            if (lowerMessage.contains(keyword)) {
                return true;
            }
        }
        
        // 4. 单独的技术词汇（如"Java"、"Python"）不触发书籍查询
        // 只有当技术词汇与"书"相关词汇组合时才触发
        String[] techKeywords = {
            "java", "python", "c++", "算法", "数据结构",
            "计算机", "编程", "开发"
        };
        
        String[] bookRelatedWords = {
            "书", "书籍", "教材", "教程书", "参考书"
        };
        
        boolean hasTechKeyword = false;
        boolean hasBookWord = false;
        
        for (String tech : techKeywords) {
            if (lowerMessage.contains(tech)) {
                hasTechKeyword = true;
                break;
            }
        }
        
        for (String book : bookRelatedWords) {
            if (lowerMessage.contains(book)) {
                hasBookWord = true;
                break;
            }
        }
        
        // 技术词汇 + 书籍词汇 = 书籍查询
        if (hasTechKeyword && hasBookWord) {
            return true;
        }
        
        // 5. 默认不是书籍查询（避免误判）
        return false;
    }
    
    /**
     * 生成包含书籍信息的回复
     */
    /**
     * 生成书籍查询回复（热情友好版）
     */
    private String generateBookQueryResponse(String query, List<BookPageVO> books) {
        StringBuilder response = new StringBuilder();
        
        // 根据查询内容和结果数量生成不同的开场白
        if (books.size() == 1) {
            response.append("太好了！为您找到了这本书，希望正是您需要的：\n\n");
        } else if (books.size() <= 3) {
            response.append(String.format("很高兴为您找到了 %d 本相关书籍，每一本都值得一看：\n\n", books.size()));
        } else {
            response.append(String.format("真棒！为您精选了 %d 本优质书籍，相信总有一本适合您：\n\n", books.size()));
        }
        
        response.append("<div class=\"book-card-list\">");
        
        for (BookPageVO book : books) {
            response.append(String.format(
                "<div class=\"book-card-item\" onclick=\"window.navigateToBook(%d)\">" +
                "  <img src=\"%s\" class=\"book-cover\" alt=\"%s\" />" +
                "  <div class=\"book-info\">" +
                "    <div class=\"book-title\">%s</div>" +
                "    <div class=\"book-author\">%s</div>" +
                "    <div class=\"book-price\">¥%.2f</div>" +
                "  </div>" +
                "</div>",
                book.getBookId(),
                book.getCoverImg() != null ? book.getCoverImg() : "/default-book-cover.png",
                book.getTitle(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice()
            ));
        }
        
        response.append("</div>");
        
        // 添加友好的结尾提示
        response.append("\n\n💡 温馨提示：点击书籍卡片可以查看详细信息哦！如果没有找到心仪的书籍，可以换个关键词试试～");
        
        return response.toString();
    }
    
    /**
     * 获取成色文本
     */
    private String getConditionText(Integer condition) {
        if (condition == null) return "未知";
        switch (condition) {
            case 1: return "全新";
            case 2: return "九成新";
            case 3: return "八成新";
            case 4: return "明显使用痕迹";
            default: return "未知";
        }
    }

    @Override
    public List<AiChatSessionVO> getSessionList() {
        Long userId = UserContext.getUserId();
        
        List<AiChatSession> sessions = sessionMapper.selectListByUserId(userId);
        
        return sessions.stream()
                .map(this::convertSessionToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AiChatMessageVO> getSessionMessages(Long sessionId) {
        Long userId = UserContext.getUserId();
        
        // 验证会话权限
        AiChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }
        
        List<AiChatMessage> messages = messageMapper.selectListBySessionId(sessionId);
        
        return messages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AiChatSessionVO createSession() {
        Long userId = UserContext.getUserId();
        
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        // session.setTitle("新对话");
        session.setMessageCount(0);
        
        sessionMapper.insert(session);
        
        return convertSessionToVO(session);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId) {
        Long userId = UserContext.getUserId();
        
        // 验证会话权限
        AiChatSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }
        
        // 删除会话（消息会级联删除）
        sessionMapper.deleteById(sessionId);
        
        log.info("删除AI对话会话，sessionId: {}", sessionId);
    }

    /**
     * 获取最近的N条消息
     */
    private List<AiChatMessage> getRecentMessages(Long sessionId, int limit) {
        List<AiChatMessage> messages = messageMapper.selectRecentMessages(sessionId, limit);
        
        // 反转顺序，使其按时间正序
        List<AiChatMessage> result = new ArrayList<>(messages);
        java.util.Collections.reverse(result);
        
        return result;
    }

    /**
     * 转换消息实体为VO
     */
    private AiChatMessageVO convertToVO(AiChatMessage message) {
        AiChatMessageVO vo = new AiChatMessageVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    /**
     * 转换会话实体为VO
     */
    private AiChatSessionVO convertSessionToVO(AiChatSession session) {
        AiChatSessionVO vo = new AiChatSessionVO();
        BeanUtils.copyProperties(session, vo);
        return vo;
    }

    @Override
    @Transactional
    public Long chatStream(Long sessionId, String message, AiAssistantService.StreamCallback callback) {
        // 在主线程中获取用户ID
        Long userId = UserContext.getUserId();
        
        try {
            // 1. 如果没有会话ID，创建新会话
            if (sessionId == null) {
                AiChatSession session = new AiChatSession();
                session.setUserId(userId);
                // session.setTitle("AI助手对话");
                session.setMessageCount(0);
                sessionMapper.insert(session);
                sessionId = session.getId();
                log.info("创建新的AI对话会话，sessionId: {}", sessionId);
            } else {
                // 验证会话是否属于当前用户
                AiChatSession session = sessionMapper.selectById(sessionId);
                if (session == null || !session.getUserId().equals(userId)) {
                    callback.onError("会话不存在或无权访问");
                    return null;
                }
            }
            
            // 2. 保存用户消息
            AiChatMessage userMessage = new AiChatMessage();
            userMessage.setSessionId(sessionId);
            userMessage.setRole("user");
            userMessage.setContent(message);
            messageMapper.insert(userMessage);
            
            final Long finalSessionId = sessionId;
            
            // 3. 检查是否是书籍查询请求
            if (isBookQueryIntent(message)) {
                // 这是书籍查询请求,调用AI查询服务
                log.info("检测到书籍查询意图(流式): {}", message);
                try {
                    IPage<BookPageVO> bookPage = aiQueryService.queryBooksByNaturalLanguage(message, 1, 5);
                    String aiReply;
                    
                    if (bookPage != null && bookPage.getRecords() != null && !bookPage.getRecords().isEmpty()) {
                        List<BookPageVO> bookResults = bookPage.getRecords();
                        // 生成包含书籍信息的回复
                        aiReply = generateBookQueryResponse(message, bookResults);
                        
                        // 流式输出回复文本
                        // 这里一次性输出，避免HTML标签被拆分导致渲染异常
                        callback.onNext(aiReply);
                    } else {
                        aiReply = "抱歉,没有找到符合您要求的书籍。您可以尝试:\n" +
                                 "1. 使用更具体的关键词\n" +
                                 "2. 调整价格范围\n" +
                                 "3. 浏览其他分类";
                        
                        // 流式输出回复文本
                        String[] words = aiReply.split("");
                        for (String word : words) {
                            callback.onNext(word);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    
                    // 保存AI回复
                    AiChatMessage assistantMessage = new AiChatMessage();
                    assistantMessage.setSessionId(finalSessionId);
                    assistantMessage.setRole("assistant");
                    assistantMessage.setContent(aiReply);
                    messageMapper.insert(assistantMessage);
                    
                    // 更新会话信息
                    updateSessionInfo(finalSessionId, message);
                    
                    // 完成回调
                    callback.onComplete(finalSessionId, aiReply);
                    
                } catch (Exception e) {
                    log.error("书籍查询失败: {}", e.getMessage(), e);
                    String errorReply = "抱歉,查询书籍时出现了问题,请稍后再试。";
                    callback.onNext(errorReply);
                    callback.onComplete(finalSessionId, errorReply);
                }
            } else {
                // 4. 普通对话,获取历史消息（最近10条）
                List<AiChatMessage> historyMessages = getRecentMessages(sessionId, 10);
                
                // 5. 调用通义千问流式获取回复
                qwenService.chatWithContextStream(message, historyMessages, new QwenService.StreamCallback() {
                    @Override
                    public void onNext(String text) {
                        // 转发流式文本片段
                        callback.onNext(text);
                    }
                    
                    @Override
                    public void onComplete(String fullText) {
                        try {
                            // 保存AI回复
                            AiChatMessage assistantMessage = new AiChatMessage();
                            assistantMessage.setSessionId(finalSessionId);
                            assistantMessage.setRole("assistant");
                            assistantMessage.setContent(fullText);
                            messageMapper.insert(assistantMessage);
                            
                            // 更新会话信息
                            updateSessionInfo(finalSessionId, message);
                            
                            // 完成回调
                            callback.onComplete(finalSessionId, fullText);
                        } catch (Exception e) {
                            log.error("保存AI回复失败: {}", e.getMessage(), e);
                            callback.onError("保存消息失败");
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }
            
            return sessionId;
            
        } catch (Exception e) {
            log.error("AI助手流式聊天失败: {}", e.getMessage(), e);
            callback.onError("聊天失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 更新会话信息
     */
    private void updateSessionInfo(Long sessionId, String message) {
        AiChatSession session = sessionMapper.selectById(sessionId);
        session.setLastMessage(message.length() > 100 ? message.substring(0, 100) + "..." : message);
        session.setMessageCount(session.getMessageCount() + 2);
        
        // 自动生成会话标题（第一次对话时）
        if (session.getMessageCount() <= 2 && (session.getTitle() == null || "AI助手对话".equals(session.getTitle()) || "新对话".equals(session.getTitle()))) {
            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
            session.setTitle(title);
            sessionMapper.updateById(session);
        } else {
             sessionMapper.updateById(session);
        }
    }
}
