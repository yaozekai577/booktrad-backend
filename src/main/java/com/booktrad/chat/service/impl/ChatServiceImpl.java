package com.booktrad.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.booktrad.chat.dto.ChatMessageSendDTO;
import com.booktrad.chat.dto.ChatSessionCreateDTO;
import com.booktrad.chat.entity.ChatMessage;
import com.booktrad.chat.entity.ChatSession;
import com.booktrad.chat.mapper.ChatMessageMapper;
import com.booktrad.chat.mapper.ChatSessionMapper;
import com.booktrad.chat.service.ChatService;
import com.booktrad.chat.vo.ChatMessageVO;
import com.booktrad.chat.vo.ChatSessionVO;
import com.booktrad.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天业务实现类
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO createOrGetSession(ChatSessionCreateDTO createDTO) {
        Long currentUserId = UserContext.getUserId();

        // 查询是否已存在会话
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getBookId, createDTO.getBookId())
                .eq(ChatSession::getBuyerId, currentUserId)
                .eq(ChatSession::getSellerId, createDTO.getSellerId())
                .eq(ChatSession::getStatus, 1);

        ChatSession existSession = chatSessionMapper.selectOne(queryWrapper);

        if (existSession != null) {
            // 返回已存在的会话
            return chatSessionMapper.selectSessionDetailById(existSession.getId(), currentUserId);
        }

        // 创建新会话
        ChatSession session = new ChatSession();
        session.setBookId(createDTO.getBookId());
        session.setBuyerId(currentUserId);
        session.setSellerId(createDTO.getSellerId());
        session.setStatus(1);
        session.setLastMessageTime(LocalDateTime.now());

        chatSessionMapper.insert(session);

        return chatSessionMapper.selectSessionDetailById(session.getId(), currentUserId);
    }

    @Override
    public List<ChatSessionVO> getSessionList() {
        Long currentUserId = UserContext.getUserId();
        List<ChatSessionVO> sessionList = chatSessionMapper.selectSessionListByUserId(currentUserId);

        // 为每个会话统计未读消息数
        for (ChatSessionVO session : sessionList) {
            Integer unreadCount = chatMessageMapper.countUnreadMessages(session.getId(), currentUserId);
            session.setUnreadCount(unreadCount);
        }

        return sessionList;
    }

    @Override
    public ChatSessionVO getSessionDetail(Long sessionId) {
        Long currentUserId = UserContext.getUserId();
        return chatSessionMapper.selectSessionDetailById(sessionId, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessageVO sendMessage(ChatMessageSendDTO sendDTO) {
        Long currentUserId = UserContext.getUserId();

        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(sendDTO.getSessionId());
        message.setSenderId(currentUserId);
        message.setMessageType(sendDTO.getMessageType());
        message.setContent(sendDTO.getContent());
        message.setIsRead(0);

        chatMessageMapper.insert(message);

        // 更新会话的最后消息信息
        ChatSession session = new ChatSession();
        session.setId(sendDTO.getSessionId());
        session.setLastMessage(sendDTO.getContent().length() > 50 
                ? sendDTO.getContent().substring(0, 50) + "..." 
                : sendDTO.getContent());
        session.setLastMessageTime(LocalDateTime.now());
        chatSessionMapper.updateById(session);

        // 查询并返回消息详情
        List<ChatMessageVO> messages = chatMessageMapper.selectMessageListBySessionId(sendDTO.getSessionId());
        return messages.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ChatMessageVO> getMessageList(Long sessionId) {
        return chatMessageMapper.selectMessageListBySessionId(sessionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(Long sessionId) {
        Long currentUserId = UserContext.getUserId();
        
        System.out.println("=== 标记已读 Debug 信息 ===");
        System.out.println("sessionId: " + sessionId);
        System.out.println("currentUserId: " + currentUserId);

        // 先查询该会话的所有消息（不加任何过滤条件）
        LambdaQueryWrapper<ChatMessage> allMessagesQuery = new LambdaQueryWrapper<>();
        allMessagesQuery.eq(ChatMessage::getSessionId, sessionId);
        List<ChatMessage> allMessages = chatMessageMapper.selectList(allMessagesQuery);
        System.out.println("该会话所有消息数: " + allMessages.size());
        for (ChatMessage msg : allMessages) {
            System.out.println("消息ID: " + msg.getId() + ", 发送者: " + msg.getSenderId() + ", 已读: " + msg.getIsRead());
        }

        // 先查询需要标记为已读的消息
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getSessionId, sessionId)
                .ne(ChatMessage::getSenderId, currentUserId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> unreadMessages = chatMessageMapper.selectList(queryWrapper);
        System.out.println("需要标记为已读的消息数: " + unreadMessages.size());
        
        // 如果有未读消息，批量更新为已读
        if (!unreadMessages.isEmpty()) {
            for (ChatMessage message : unreadMessages) {
                System.out.println("正在标记消息ID: " + message.getId() + " 为已读");
                message.setIsRead(1);
                chatMessageMapper.updateById(message);
            }
            System.out.println("已成功标记 " + unreadMessages.size() + " 条消息为已读");
        } else {
            System.out.println("没有需要标记的消息");
        }
        System.out.println("=== Debug 信息结束 ===");
    }
}
