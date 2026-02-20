package com.booktrad.order.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 评价视图对象
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class ReviewVO {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 评价人ID
     */
    private Long reviewerId;

    /**
     * 评价人用户名
     */
    private String reviewerUsername;

    /**
     * 被评价人ID
     */
    private Long revieweeId;

    /**
     * 被评价人用户名
     */
    private String revieweeUsername;

    /**
     * 评价人角色：1买家评价卖家 2卖家评价买家
     */
    private Integer reviewerRole;

    /**
     * 评分：1-5星
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 是否匿名：0否 1是
     */
    private Integer isAnonymous;

    /**
     * 回复内容
     */
    private String replyContent;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;
}
