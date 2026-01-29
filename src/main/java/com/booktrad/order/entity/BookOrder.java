package com.booktrad.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单实体类
 * @Date 2026/1/24 11:36
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@TableName("book_order")
public class BookOrder {

    /**
     * 订单ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 买家用户ID
     */
    @TableField("buyer_id")
    private Long buyerId;

    /**
     * 卖家用户ID
     */
    @TableField("seller_id")
    private Long sellerId;

    /**
     * 书籍ID
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 书籍标题（冗余）
     */
    @TableField("book_title")
    private String bookTitle;

    /**
     * 书籍封面（冗余）
     */
    @TableField("book_cover")
    private String bookCover;

    /**
     * 书籍原价（冗余）
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 成交价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 交易方式：1线下面交
     */
    @TableField("trade_type")
    private Integer tradeType;

    /**
     * 约定面交地点
     */
    @TableField("meet_location")
    private String meetLocation;

    /**
     * 约定面交时间
     */
    @TableField("meet_time")
    private LocalDateTime meetTime;

    /**
     * 买家联系电话（冗余）
     */
    @TableField("buyer_phone")
    private String buyerPhone;

    /**
     * 卖家联系电话（冗余）
     */
    @TableField("seller_phone")
    private String sellerPhone;

    /**
     * 订单备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
     * 订单状态：1待确认 2已确认、待交易 3已完成 4已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 买家是否确认收货：0未确认 1已确认
     */
    @TableField("buyer_confirmed")
    private Integer buyerConfirmed;

    /**
     * 卖家是否确认交货：0未确认 1已确认
     */
    @TableField("seller_confirmed")
    private Integer sellerConfirmed;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 卖家确认接单时间
     */
    @TableField("confirmed_at")
    private LocalDateTime confirmedAt;

    /**
     * 买家确认收货时间
     */
    @TableField("buyer_confirmed_at")
    private LocalDateTime buyerConfirmedAt;

    /**
     * 卖家确认交货时间
     */
    @TableField("seller_confirmed_at")
    private LocalDateTime sellerConfirmedAt;

    /**
     * 买家是否已评价：0未评价 1已评价
     */
    @TableField("buyer_reviewed")
    private Integer buyerReviewed;

    /**
     * 卖家是否已评价：0未评价 1已评价
     */
    @TableField("seller_reviewed")
    private Integer sellerReviewed;

    /**
     * 订单完成时间（双方都确认后）
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 取消时间
     */
    @TableField("cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
