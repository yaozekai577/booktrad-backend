package com.booktrad.chat.websocket;

import com.booktrad.chat.vo.ChatMessageVO;
import com.booktrad.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理类
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    /**
     * 存储在线用户的WebSocket会话，key为userId
     */
    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        // 注册JavaTimeModule以支持LocalDateTime序列化
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            String query = uri.getQuery();
            // 解析token参数
            String token = null;
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    token = keyValue[1];
                    break;
                }
            }

            if (token != null) {
                try {
                    // 验证token并获取用户ID
                    Long userId = JwtUtil.getUserIdFromToken(token);
                    if (userId != null) {
                        // 存储会话
                        USER_SESSIONS.put(userId, session);
                        session.getAttributes().put("userId", userId);
                        return;
                    }
                } catch (Exception e) {
                    // token无效
                }
            }
        }
        // 如果未通过验证，关闭连接
        session.close(CloseStatus.POLICY_VIOLATION);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            USER_SESSIONS.remove(userId);
        }
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId 接收者ID
     * @param message 消息内容
     */
    public void sendMessageToUser(Long userId, ChatMessageVO message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                WebSocketEvent<ChatMessageVO> event = new WebSocketEvent<>("NEW_MESSAGE", message);
                String jsonMsg = objectMapper.writeValueAsString(event);
                session.sendMessage(new TextMessage(jsonMsg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送已读回执给指定用户
     *
     * @param userId 接收者ID
     * @param sessionId 会话ID
     */
    public void sendReadReceiptToUser(Long userId, Long sessionId) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                WebSocketEvent<Long> event = new WebSocketEvent<>("READ_RECEIPT", sessionId);
                String jsonMsg = objectMapper.writeValueAsString(event);
                session.sendMessage(new TextMessage(jsonMsg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
