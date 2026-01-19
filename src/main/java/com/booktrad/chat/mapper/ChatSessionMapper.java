package com.booktrad.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.chat.entity.ChatSession;
import com.booktrad.chat.vo.ChatSessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天会话Mapper
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {

    /**
     * 查询用户的会话列表（带详细信息）
     */
    List<ChatSessionVO> selectSessionListByUserId(@Param("userId") Long userId);

    /**
     * 查询会话详情（带详细信息）
     */
    ChatSessionVO selectSessionDetailById(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
}
