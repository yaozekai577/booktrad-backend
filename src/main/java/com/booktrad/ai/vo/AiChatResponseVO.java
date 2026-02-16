package com.booktrad.ai.vo;

import com.booktrad.book.vo.BookPageVO;
import lombok.Data;

import java.util.List;

/**
 * AI聊天响应VO
 */
@Data
public class AiChatResponseVO {
    
    private Long sessionId; // 会话ID
    
    private String reply; // AI回复内容
    
    private AiChatMessageVO userMessage; // 用户消息
    
    private AiChatMessageVO assistantMessage; // AI消息
    
    private List<BookPageVO> bookResults; // 书籍查询结果（如果是书籍查询）
}
