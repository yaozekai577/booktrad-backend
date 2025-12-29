package com.booktrad.book.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 添加书籍请求DTO，用于接收创建书籍的请求参数
 * @Date 2025/12/24 22:00
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookAddDTO {

    /**
     * 卖家用户ID（关联 user 表）
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
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 交易方式：自取/邮寄
     */
    private String tradeType;

    /**
     * 瑕疵说明
     */
    private String defectDesc;

    /**
     * 出版时间 yyyy-MM
     */
    private String publishTime;

    /**
     * 分类路径
     */
    private String categoryPath;

    /**
     * 状态：1-在售 2-已下架 3-已售出（可选，默认1-在售）
     */
    private Integer status;
}