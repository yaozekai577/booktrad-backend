package com.booktrad.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 聊天会话VO
 * @Date 2026/01/19
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class ChatSessionVO {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 关联书籍ID
     */
    private Long bookId;

    /**
     * 书籍标题
     */
    private String bookTitle;

    /**
     * 书籍封面
     */
    private String bookCover;

    /**
     * 买家用户ID
     */
    private Long buyerId;

    /**
     * 买家用户名
     */
    private String buyerName;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 卖家用户名
     */
    private String sellerName;

    /**
     * 对方用户ID（当前用户视角）
     */
    private Long otherUserId;

    /**
     * 对方用户名（当前用户视角）
     */
    private String otherUserName;

    /**
     * 最后一条消息摘要
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 状态：1正常 2关闭
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
