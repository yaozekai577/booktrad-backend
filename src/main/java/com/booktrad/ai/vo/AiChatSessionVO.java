package com.booktrad.ai.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI对话会话VO
 */
@Data
public class AiChatSessionVO {
    
    private Long id;
    
    private String title;
    
    private String lastMessage;
    
    private Integer messageCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
