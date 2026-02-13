package com.booktrad.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.ai.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话会话Mapper
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSession> {
}
