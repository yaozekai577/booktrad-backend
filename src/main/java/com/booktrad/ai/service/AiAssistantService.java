package com.booktrad.ai.service;

import com.booktrad.ai.vo.AiChatMessageVO;
import com.booktrad.ai.vo.AiChatResponseVO;
import com.booktrad.ai.vo.AiChatSessionVO;

import java.util.List;

/**
 * AI智能助手服务接口
 */
public interface AiAssistantService {
    
    /**
     * 发送消息给AI助手
     * 
     * @param sessionId 会话ID，首次聊天可为null
     * @param message 用户消息
     * @return AI回复
     */
    AiChatResponseVO chat(Long sessionId, String message);
    
    /**
     * 获取用户的会话列表
     * 
     * @return 会话列表
     */
    List<AiChatSessionVO> getSessionList();
    
    /**
     * 获取会话的历史消息
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessageVO> getSessionMessages(Long sessionId);
    
    /**
     * 创建新会话
     * 
     * @return 新会话
     */
    AiChatSessionVO createSession();
    
    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     */
    void deleteSession(Long sessionId);
    
    /**
     * 流式发送消息给AI助手
     * 
     * @param sessionId 会话ID，首次聊天可为null
     * @param message 用户消息
     * @param callback 流式回调
     * @return 会话ID
     */
    Long chatStream(Long sessionId, String message, StreamCallback callback);
    
    /**
     * 流式输出回调接口
     */
    interface StreamCallback {
        void onNext(String text);
        void onComplete(Long sessionId, String fullText);
        void onError(String error);
    }
}
