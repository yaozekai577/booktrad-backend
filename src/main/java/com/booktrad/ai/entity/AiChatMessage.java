package com.booktrad.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI对话消息实体类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@TableName("ai_chat_message")
public class AiChatMessage {
    
    /**
     * 消息ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 会话ID（关联 ai_chat_session 表）
     * 对应数据库字段：session_id
     */
    @TableField("session_id")
    private Long sessionId;
    
    /**
     * 消息角色
     * 对应数据库字段：role
     * 取值：user（用户消息）或 assistant（AI回复）
     */
    @TableField("role")
    private String role;
    
    /**
     * 消息内容
     * 对应数据库字段：content
     */
    @TableField("content")
    private String content;
    
    /**
     * 创建时间
     * 对应数据库字段：create_time
     * 自动填充：插入时
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
