package com.booktrad.ai.vo;

import lombok.Data;

/**
 * AI聊天响应VO
 */
@Data
public class AiChatResponseVO {
    
    private Long sessionId; // 会话ID
    
    private String reply; // AI回复内容
    
    private AiChatMessageVO userMessage; // 用户消息
    
    private AiChatMessageVO assistantMessage; // AI消息
}
