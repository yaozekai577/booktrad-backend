package com.booktrad.ai.mapper;

import com.booktrad.ai.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI对话消息Mapper
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface AiChatMessageMapper {
    
    /**
     * 插入消息
     * @param message 消息实体
     * @return 影响行数
     */
    int insert(AiChatMessage message);
    
    /**
     * 根据会话ID查询消息列表（按创建时间正序）
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> selectListBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 查询会话的最近N条消息（按创建时间倒序，用于上下文）
     * @param sessionId 会话ID
     * @param limit 数量限制
     * @return 消息列表
     */
    List<AiChatMessage> selectRecentMessages(@Param("sessionId") Long sessionId, @Param("limit") int limit);
}
