package com.booktrad.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.chat.entity.ChatMessage;
import com.booktrad.chat.vo.ChatMessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天消息Mapper
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 查询会话的消息列表（带用户信息）
     */
    List<ChatMessageVO> selectMessageListBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 统计会话未读消息数
     */
    Integer countUnreadMessages(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
}
