package com.booktrad.ai.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI对话消息VO
 */
@Data
public class AiChatMessageVO {
    
    private Long id;
    
    private Long sessionId;
    
    private String role; // user 或 assistant
    
    private String content;
    
    private LocalDateTime createTime;
}
