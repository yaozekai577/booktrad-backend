package com.booktrad.ai.dto;

import lombok.Data;

/**
 * AI聊天请求DTO
 */
@Data
public class AiChatDTO {
    
    private Long sessionId; // 会话ID，首次聊天可为空
    
    private String message; // 用户消息
}
