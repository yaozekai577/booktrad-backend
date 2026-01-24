package com.booktrad.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 创建订单DTO
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class OrderCreateDTO {

    /**
     * 书籍ID
     */
    private Long bookId;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 成交价
     */
    private BigDecimal price;

    /**
     * 约定面交地点
     */
    private String meetLocation;

    /**
     * 约定面交时间
     */
    private LocalDateTime meetTime;

    /**
     * 买家联系电话
     */
    private String buyerPhone;

    /**
     * 订单备注
     */
    private String remark;
}
