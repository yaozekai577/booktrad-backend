package com.booktrad.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单VO
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家用户ID
     */
    private Long buyerId;

    /**
     * 买家用户名
     */
    private String buyerName;

    /**
     * 买家联系电话
     */
    private String buyerPhone;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 卖家用户名
     */
    private String sellerName;

    /**
     * 卖家联系电话
     */
    private String sellerPhone;

    /**
     * 买家信用评分
     */
    private BigDecimal buyerRatingScore;

    /**
     * 卖家信用评分
     */
    private BigDecimal sellerRatingScore;

    /**
     * 书籍ID
     */
    private Long bookId;

    /**
     * 关联求购ID
     */
    private Long wantedId;

    /**
     * 书籍标题
     */
    private String bookTitle;

    /**
     * 书籍封面
     */
    private String bookCover;

    /**
     * 书籍原价
     */
    private BigDecimal originalPrice;

    /**
     * 成交价
     */
    private BigDecimal price;

    /**
     * 交易方式：1线下面交
     */
    private Integer tradeType;

    /**
     * 约定面交地点
     */
    private String meetLocation;

    /**
     * 约定面交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime meetTime;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单状态：1待确认 2已确认、待交易 3已完成 4已取消
     */
    private Integer status;

    /**
     * 订单状态文本（前端展示用）
     */
    private String statusText;

    /**
     * 买家是否确认收货：0未确认 1已确认
     */
    private Integer buyerConfirmed;

    /**
     * 卖家确认交货：0未确认 1已确认
     */
    private Integer sellerConfirmed;

    /**
     * 买家是否已评价：0未评价 1已评价
     */
    private Integer buyerReviewed;

    /**
     * 卖家是否已评价：0未评价 1已评价
     */
    private Integer sellerReviewed;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 卖家确认接单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmedAt;

    /**
     * 买家确认收货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyerConfirmedAt;

    /**
     * 卖家确认交货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sellerConfirmedAt;

    /**
     * 订单完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelledAt;
}
