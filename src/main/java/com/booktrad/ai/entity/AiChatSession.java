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
 * @Description AI对话会话实体类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@TableName("ai_chat_session")
public class AiChatSession {
    
    /**
     * 会话ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（关联 user 表）
     * 对应数据库字段：user_id
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 会话标题
     * 对应数据库字段：title
     * 默认值：AI助手对话
     */
    @TableField(value = "title", updateStrategy = FieldStrategy.NOT_NULL)
    private String title;
    
    /**
     * 最后一条消息内容（用于列表展示）
     * 对应数据库字段：last_message
     */
    @TableField("last_message")
    private String lastMessage;
    
    /**
     * 消息数量
     * 对应数据库字段：message_count
     */
    @TableField("message_count")
    private Integer messageCount;
    
    /**
     * 创建时间
     * 对应数据库字段：create_time
     * 自动填充：插入时
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     * 对应数据库字段：update_time
     * 自动填充：插入和更新时
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
