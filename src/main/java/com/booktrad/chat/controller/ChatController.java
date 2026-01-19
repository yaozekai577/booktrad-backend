package com.booktrad.chat.controller;

import com.booktrad.chat.dto.ChatMessageSendDTO;
import com.booktrad.chat.dto.ChatSessionCreateDTO;
import com.booktrad.chat.service.ChatService;
import com.booktrad.chat.vo.ChatMessageVO;
import com.booktrad.chat.vo.ChatSessionVO;
import com.booktrad.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天控制器
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 创建或获取会话
     */
    @PostMapping("/session")
    public Result<ChatSessionVO> createOrGetSession(@RequestBody ChatSessionCreateDTO createDTO) {
        ChatSessionVO sessionVO = chatService.createOrGetSession(createDTO);
        return Result.success(sessionVO);
    }

    /**
     * 获取用户的会话列表
     */
    @GetMapping("/sessions")
    public Result<List<ChatSessionVO>> getSessionList() {
        List<ChatSessionVO> sessionList = chatService.getSessionList();
        return Result.success(sessionList);
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/session/{sessionId}")
    public Result<ChatSessionVO> getSessionDetail(@PathVariable Long sessionId) {
        ChatSessionVO sessionVO = chatService.getSessionDetail(sessionId);
        return Result.success(sessionVO);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    public Result<ChatMessageVO> sendMessage(@RequestBody ChatMessageSendDTO sendDTO) {
        ChatMessageVO messageVO = chatService.sendMessage(sendDTO);
        return Result.success(messageVO);
    }

    /**
     * 获取会话的消息列表
     */
    @GetMapping("/messages/{sessionId}")
    public Result<List<ChatMessageVO>> getMessageList(@PathVariable Long sessionId) {
        List<ChatMessageVO> messageList = chatService.getMessageList(sessionId);
        return Result.success(messageList);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/messages/read/{sessionId}")
    public Result<Void> markMessagesAsRead(@PathVariable Long sessionId) {
        chatService.markMessagesAsRead(sessionId);
        return Result.success();
    }
}
