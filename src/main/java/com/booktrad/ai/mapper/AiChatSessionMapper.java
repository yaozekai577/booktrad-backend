package com.booktrad.ai.mapper;

import com.booktrad.ai.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI对话会话Mapper
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface AiChatSessionMapper {
    
    /**
     * 插入会话
     * @param session 会话实体
     * @return 影响行数
     */
    int insert(AiChatSession session);
    
    /**
     * 根据ID查询会话
     * @param id 会话ID
     * @return 会话实体
     */
    AiChatSession selectById(@Param("id") Long id);
    
    /**
     * 根据用户ID查询会话列表（按更新时间倒序）
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiChatSession> selectListByUserId(@Param("userId") Long userId);
    
    /**
     * 更新会话信息
     * @param session 会话实体
     * @return 影响行数
     */
    int updateById(AiChatSession session);
    
    /**
     * 根据ID删除会话
     * @param id 会话ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
