package com.booktrad.ai.service.impl;

import com.booktrad.ai.entity.AiChatMessage;
import com.booktrad.ai.entity.AiChatSession;
import com.booktrad.ai.mapper.AiChatMessageMapper;
import com.booktrad.ai.mapper.AiChatSessionMapper;
import com.booktrad.ai.service.AiAssistantService;
import com.booktrad.ai.service.QwenService;
import com.booktrad.ai.vo.AiChatMessageVO;
import com.booktrad.ai.vo.AiChatResponseVO;
import com.booktrad.ai.vo.AiChatSessionVO;
import com.booktrad.common.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    @Override
    @Transactional
    public AiChatResponseVO chat(Long sessionId, String message) {
        Long userId = UserContext.getUserId();
        
        // 1. 如果没有会话ID，创建新会话
        if (sessionId == null) {
            AiChatSession session = new AiChatSession();
            session.setUserId(userId);
            session.setTitle("AI助手对话");
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
        
        // 3. 获取历史消息（最近10条）
        List<AiChatMessage> historyMessages = getRecentMessages(sessionId, 10);
        
        // 4. 调用通义千问获取回复
        String aiReply = qwenService.chatWithContext(message, historyMessages);
        
        // 5. 保存AI回复
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(aiReply);
        messageMapper.insert(assistantMessage);
        
        // 6. 更新会话信息
        AiChatSession session = sessionMapper.selectById(sessionId);
        session.setLastMessage(message.length() > 100 ? message.substring(0, 100) + "..." : message);
        session.setMessageCount(session.getMessageCount() + 2);
        
        // 自动生成会话标题（第一次对话时）
        if (session.getMessageCount() == 2 && "AI助手对话".equals(session.getTitle())) {
            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
            session.setTitle(title);
        }
        
        sessionMapper.updateById(session);
        
        // 7. 构造返回结果
        AiChatResponseVO response = new AiChatResponseVO();
        response.setSessionId(sessionId);
        response.setReply(aiReply);
        response.setUserMessage(convertToVO(userMessage));
        response.setAssistantMessage(convertToVO(assistantMessage));
        
        return response;
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
        session.setTitle("新对话");
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
                session.setTitle("AI助手对话");
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
            
            // 3. 获取历史消息（最近10条）
            List<AiChatMessage> historyMessages = getRecentMessages(sessionId, 10);
            
            // 4. 调用通义千问流式获取回复
            final Long finalSessionId = sessionId;
            final Long finalUserId = userId; // 保存userId供异步线程使用
            
            qwenService.chatWithContextStream(message, historyMessages, new QwenService.StreamCallback() {
                @Override
                public void onNext(String text) {
                    // 转发流式文本片段
                    callback.onNext(text);
                }
                
                @Override
                public void onComplete(String fullText) {
                    try {
                        // 5. 保存AI回复
                        AiChatMessage assistantMessage = new AiChatMessage();
                        assistantMessage.setSessionId(finalSessionId);
                        assistantMessage.setRole("assistant");
                        assistantMessage.setContent(fullText);
                        messageMapper.insert(assistantMessage);
                        
                        // 6. 更新会话信息
                        AiChatSession session = sessionMapper.selectById(finalSessionId);
                        session.setLastMessage(message.length() > 100 ? message.substring(0, 100) + "..." : message);
                        session.setMessageCount(session.getMessageCount() + 2);
                        
                        // 自动生成会话标题（第一次对话时）
                        if (session.getMessageCount() == 2 && "AI助手对话".equals(session.getTitle())) {
                            String title = message.length() > 20 ? message.substring(0, 20) + "..." : message;
                            session.setTitle(title);
                        }
                        
                        sessionMapper.updateById(session);
                        
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
            
            return sessionId;
            
        } catch (Exception e) {
            log.error("AI助手流式聊天失败: {}", e.getMessage(), e);
            callback.onError("聊天失败：" + e.getMessage());
            return null;
        }
    }
}
