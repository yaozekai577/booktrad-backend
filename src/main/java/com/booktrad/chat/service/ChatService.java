package com.booktrad.chat.service;

import com.booktrad.chat.dto.ChatMessageSendDTO;
import com.booktrad.chat.dto.ChatSessionCreateDTO;
import com.booktrad.chat.vo.ChatMessageVO;
import com.booktrad.chat.vo.ChatSessionVO;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天业务接口
 * @Date 2026/01/19 14:40
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface ChatService {

    /**
     * 创建或获取会话
     * @param createDTO 创建会话DTO
     * @return 会话详情
     */
    ChatSessionVO createOrGetSession(ChatSessionCreateDTO createDTO);

    /**
     * 获取用户的会话列表
     * @return 会话列表
     */
    List<ChatSessionVO> getSessionList();

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    ChatSessionVO getSessionDetail(Long sessionId);

    /**
     * 发送消息
     * @param sendDTO 发送消息DTO
     * @return 消息详情
     */
    ChatMessageVO sendMessage(ChatMessageSendDTO sendDTO);

    /**
     * 获取会话的消息列表
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<ChatMessageVO> getMessageList(Long sessionId);

    /**
     * 标记消息为已读
     * @param sessionId 会话ID
     */
    void markMessagesAsRead(Long sessionId);
}
