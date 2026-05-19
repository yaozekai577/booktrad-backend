package com.booktrad.favorite.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 收藏书籍列表VO，用于展示用户收藏的书籍信息
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@Data
public class FavoriteBookVO {

    /**
     * 收藏记录ID
     */
    private Long favoriteId;

    /**
     * 书籍ID
     */
    private Long bookId;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面图片（取第一张）
     */
    private String coverImage;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 书籍状态：1-在售 2-已下架 3-交易中 4-已售出
     */
    private Integer bookStatus;

    /**
     * 收藏时间
     */
    private LocalDateTime favoriteTime;
}