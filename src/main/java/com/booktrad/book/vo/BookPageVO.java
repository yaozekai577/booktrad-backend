package com.booktrad.book.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍分页查询VO，用于首页展示书籍列表（分页）
 * @Date 2025/12/30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookPageVO {

    /**
     * 书籍ID
     */
    private Long bookId;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面图片URL（取第一张）
     */
    private String coverImg;

    /**
     * 出售价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 卖家用户名
     */
    private String sellerName;

    /**
     * 卖家位置（如学院/校区）
     */
    private String sellerLocation;

    /**
     * 期望交易地点
     */
    private String preferredLocation;

    /**
     * 书籍状态：1-在售 2-已下架 3-交易中 4-已售出
     */
    private Integer status;

    /**
     * 是否封禁：0-正常 1-封禁
     */
    private Integer isBanned;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createdAt;
}
