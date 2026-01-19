package com.booktrad.chat.dto;

import lombok.Data;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 创建会话DTO
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class ChatSessionCreateDTO {

    /**
     * 书籍ID
     */
    private Long bookId;

    /**
     * 卖家用户ID
     */
    private Long sellerId;
}
