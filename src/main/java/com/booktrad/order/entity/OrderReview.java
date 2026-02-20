package com.booktrad.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单评价实体类
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@TableName("order_review")
public class OrderReview {

    /**
     * 评价ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 评价人ID
     */
    @TableField("reviewer_id")
    private Long reviewerId;

    /**
     * 被评价人ID
     */
    @TableField("reviewee_id")
    private Long revieweeId;

    /**
     * 评价人角色：1买家评价卖家 2卖家评价买家
     */
    @TableField("reviewer_role")
    private Integer reviewerRole;

    /**
     * 评分：1-5星
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 评价内容
     */
    @TableField("content")
    private String content;

    /**
     * 是否匿名：0否 1是
     */
    @TableField("is_anonymous")
    private Integer isAnonymous;

    /**
     * 回复内容（被评价人可以回复）
     */
    @TableField("reply_content")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("reply_time")
    private LocalDateTime replyTime;

    /**
     * 评价时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 状态：1-正常 0-已删除
     */
    @TableLogic(value = "1", delval = "0")
    @TableField("status")
    private Integer status;
}
