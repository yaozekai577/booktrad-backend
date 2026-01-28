package com.booktrad.book.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍更新DTO，用于接收修改书籍的请求参数
 * @Date 2026/1/26
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookUpdateDTO {

    /**
     * 书籍ID（必填）
     */
    private Long id;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * ISBN编号
     */
    private String isbn;

    /**
     * 书籍分类ID
     */
    private Long categoryId;

    /**
     * 出售价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 成色：1-全新 2-九成新 3-八成新 4-明显使用痕迹
     */
    private Integer bookCondition;

    /**
     * 书籍描述（使用情况、备注等）
     */
    private String description;

    /**
     * 瑕疵说明
     */
    private String defectDesc;

    /**
     * 封面图片URL（多个图片以逗号分隔）
     */
    private String coverImage;

    /**
     * 交易方式：自取/邮寄
     */
    private String tradeType;

    /**
     * 卖家期望交易地点
     */
    private String preferredLocation;

    /**
     * 出版时间 yyyy-MM
     */
    private String publishTime;
}
