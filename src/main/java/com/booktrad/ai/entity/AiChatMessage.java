package com.booktrad.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话消息实体
 */
@Data
@TableName("ai_chat_message")
public class AiChatMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long sessionId;
    
    private String role; // user 或 assistant
    
    private String content;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
