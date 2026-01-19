package com.booktrad.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天消息Mapper接口
 * @Date 2026/01/19 14:30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
