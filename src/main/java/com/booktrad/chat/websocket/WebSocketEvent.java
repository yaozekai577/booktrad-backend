package com.booktrad.chat.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEvent<T> {
    /**
     * 事件类型：NEW_MESSAGE, READ_RECEIPT
     */
    private String type;
    private T data;
}
