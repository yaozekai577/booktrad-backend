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
import com.booktrad.chat.websocket.ChatWebSocketHandler;
import com.booktrad.common.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
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
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatSessionVO createOrGetSession(ChatSessionCreateDTO createDTO) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        if (createDTO.getSellerId() == null) {
            throw new RuntimeException("对方用户ID不能为空");
        }
        if (currentUserId.equals(createDTO.getSellerId())) {
            throw new RuntimeException("不能和自己创建会话");
        }
        boolean hasBookContext = createDTO.getBookId() != null && createDTO.getBookId() > 0;
        boolean hasWantedContext = createDTO.getWantedId() != null && createDTO.getWantedId() > 0;
        if (!hasBookContext && !hasWantedContext) {
            throw new RuntimeException("书籍ID或求购ID至少传一个");
        }
        Long bookId = hasBookContext ? createDTO.getBookId() : 0L;
        Long wantedId = hasWantedContext ? createDTO.getWantedId() : 0L;
        Long legacyWantedBookId = (hasWantedContext && !hasBookContext) ? createDTO.getWantedId() : null;

        // 查询是否已存在会话
        LambdaQueryWrapper<ChatSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSession::getBuyerId, currentUserId)
                .eq(ChatSession::getSellerId, createDTO.getSellerId())
                .eq(ChatSession::getStatus, 1);
        if (hasWantedContext && !hasBookContext) {
            queryWrapper.and(w -> w
                    .and(v -> v.eq(ChatSession::getBookId, 0L).eq(ChatSession::getWantedId, wantedId))
                    .or(v -> v.eq(ChatSession::getBookId, legacyWantedBookId).eq(ChatSession::getWantedId, wantedId)));
        } else {
            queryWrapper.eq(ChatSession::getBookId, bookId)
                    .eq(ChatSession::getWantedId, wantedId);
        }
        queryWrapper.last("ORDER BY created_at DESC LIMIT 1");

        ChatSession existSession = chatSessionMapper.selectOne(queryWrapper);

        if (existSession != null) {
            // 返回已存在的会话
            return chatSessionMapper.selectSessionDetailById(existSession.getId(), currentUserId);
        }

        // 创建新会话
        ChatSession session = new ChatSession();
        session.setBookId(bookId);
        session.setWantedId(wantedId);
        session.setBuyerId(currentUserId);
        session.setSellerId(createDTO.getSellerId());
        session.setStatus(1);
        session.setLastMessageTime(LocalDateTime.now());

        try {
            chatSessionMapper.insert(session);
        } catch (DuplicateKeyException e) {
            ChatSession retrySession = chatSessionMapper.selectOne(queryWrapper);
            if (retrySession != null) {
                return chatSessionMapper.selectSessionDetailById(retrySession.getId(), currentUserId);
            }
            throw new RuntimeException("会话创建失败：聊天表唯一索引与当前逻辑不一致，请执行数据库迁移脚本后重试");
        }

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

        ChatSession session = new ChatSession();
        session.setId(sendDTO.getSessionId());
        String lastMessage;
        if (sendDTO.getMessageType() != null && sendDTO.getMessageType() == 2) {
            lastMessage = "[图片]";
        } else {
            String content = sendDTO.getContent() == null ? "" : sendDTO.getContent();
            lastMessage = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        }
        session.setLastMessage(lastMessage);
        session.setLastMessageTime(LocalDateTime.now());
        chatSessionMapper.updateById(session);

        // 查询并返回消息详情
        List<ChatMessageVO> messages = chatMessageMapper.selectMessageListBySessionId(sendDTO.getSessionId());
        ChatMessageVO messageVO = messages.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst()
                .orElse(null);

        // 通过WebSocket推送消息给对方
        if (messageVO != null) {
            ChatSession currentSession = chatSessionMapper.selectById(sendDTO.getSessionId());
            if (currentSession != null) {
                Long recipientId = currentSession.getBuyerId().equals(currentUserId) 
                        ? currentSession.getSellerId() 
                        : currentSession.getBuyerId();
                chatWebSocketHandler.sendMessageToUser(recipientId, messageVO);
            }
        }

        return messageVO;
    }

    @Override
    public List<ChatMessageVO> getMessageList(Long sessionId) {
        Long currentUserId = UserContext.getUserId();
        
        // 获取消息列表
        List<ChatMessageVO> messageList = chatMessageMapper.selectMessageListBySessionId(sessionId);
        
        // 自动标记对方发给我的未读消息为已读
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getSessionId, sessionId)
                .ne(ChatMessage::getSenderId, currentUserId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> unreadMessages = chatMessageMapper.selectList(queryWrapper);
        
        if (!unreadMessages.isEmpty()) {
            // 批量更新为已读
            for (ChatMessage message : unreadMessages) {
                message.setIsRead(1);
                chatMessageMapper.updateById(message);
            }
            
            // 通知对方消息已读（通过WebSocket）
            ChatSession session = chatSessionMapper.selectById(sessionId);
            if (session != null) {
                Long otherUserId = session.getBuyerId().equals(currentUserId) 
                        ? session.getSellerId() 
                        : session.getBuyerId();
                chatWebSocketHandler.sendReadReceiptToUser(otherUserId, sessionId);
            }
            
            // 更新返回的消息列表中的已读状态
            for (ChatMessageVO messageVO : messageList) {
                if (!messageVO.getSenderId().equals(currentUserId)) {
                    messageVO.setIsRead(1);
                }
            }
        }
        
        return messageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(Long sessionId) {
        Long currentUserId = UserContext.getUserId();

        // 查询需要标记为已读的消息（对方发给我的未读消息）
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getSessionId, sessionId)
                .ne(ChatMessage::getSenderId, currentUserId)
                .eq(ChatMessage::getIsRead, 0);
        
        List<ChatMessage> unreadMessages = chatMessageMapper.selectList(queryWrapper);
        
        // 如果有未读消息，批量更新为已读
        if (!unreadMessages.isEmpty()) {
            for (ChatMessage message : unreadMessages) {
                message.setIsRead(1);
                chatMessageMapper.updateById(message);
            }
            
            // 通知对方消息已读（通过WebSocket）
            ChatSession session = chatSessionMapper.selectById(sessionId);
            if (session != null) {
                Long otherUserId = session.getBuyerId().equals(currentUserId) 
                        ? session.getSellerId() 
                        : session.getBuyerId();
                chatWebSocketHandler.sendReadReceiptToUser(otherUserId, sessionId);
            }
        }
    }
}
