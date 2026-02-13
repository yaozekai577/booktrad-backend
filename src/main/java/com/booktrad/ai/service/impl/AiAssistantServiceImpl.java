package com.booktrad.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getUserId, userId)
               .orderByDesc(AiChatSession::getUpdateTime);
        
        List<AiChatSession> sessions = sessionMapper.selectList(wrapper);
        
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
        
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId)
               .orderByAsc(AiChatMessage::getCreateTime);
        
        List<AiChatMessage> messages = messageMapper.selectList(wrapper);
        
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
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId)
               .orderByDesc(AiChatMessage::getCreateTime)
               .last("LIMIT " + limit);
        
        List<AiChatMessage> messages = messageMapper.selectList(wrapper);
        
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
}
