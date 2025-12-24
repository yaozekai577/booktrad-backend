package com.booktrad.book.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 更新书籍请求DTO，用于接收更新书籍的请求参数
 * @Date 2025/12/24 22:00
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookUpdateDTO {

    /**
     * 书籍ID，用于标识要更新的书籍
     */
    private Long id;

    /**
     * 书名（可选）
     */
    private String title;

    /**
     * 作者（可选）
     */
    private String author;

    /**
     * 出版社（可选）
     */
    private String publisher;

    /**
     * ISBN编号（可选）
     */
    private String isbn;

    /**
     * 书籍分类ID（可选）
     */
    private Long categoryId;

    /**
     * 出售价格（可选）
     */
    private BigDecimal price;

    /**
     * 原价（可选）
     */
    private BigDecimal originalPrice;

    /**
     * 成色：1-全新 2-九成新 3-八成新 4-明显使用痕迹（可选）
     */
    private Integer bookCondition;

    /**
     * 书籍描述（使用情况、备注等）（可选）
     */
    private String description;

    /**
     * 封面图片URL（可选）
     */
    private String coverImage;

    /**
     * 状态：1-在售 2-已下架 3-已售出（可选）
     */
    private Integer status;
}