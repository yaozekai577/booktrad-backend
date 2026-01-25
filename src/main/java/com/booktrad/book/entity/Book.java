package com.booktrad.book.entity;

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
 * @Description 二手书籍实体类
 * @Date 2025/12/24 21:49
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
@TableName("book")
public class Book {

    /**
     * 书籍ID，主键，自增
     */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    /**
     * 卖家用户ID（关联 user 表）
     */
    @TableField("seller_id")
    private Long sellerId;

    /**
     * 书名
     * 对应数据库字段：title
     */
    @TableField("title")
    private String title;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 出版社
     */
    @TableField("publisher")
    private String publisher;

    /**
     * ISBN编号
     */
    @TableField("isbn")
    private String isbn;

    /**
     * 书籍分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 出售价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 成色：1-全新 2-九成新 3-八成新 4-明显使用痕迹
     */
    @TableField("book_condition")
    private Integer bookCondition;

    /**
     * 书籍描述（使用情况、备注等）
     */
    @TableField("description")
    private String description;

    /**
     * 封面图片URL
     * 对应数据库字段：cover_image
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 状态：1-在售 2-已下架 3-交易中 4-已售出
     * 对应数据库字段：status
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否封禁：0-正常 1-封禁
     */
    @TableField("is_banned")
    private Integer isBanned;

    /**
     * 浏览次数
     * 对应数据库字段：view_count
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 交易方式：自取/邮寄
     * 对应数据库字段：trade_type
     */
    @TableField("trade_type")
    private String tradeType;

    /**
     * 瑕疵说明
     * 对应数据库字段：defect_desc
     */
    @TableField("defect_desc")
    private String defectDesc;

    /**
     * 出版时间 yyyy-MM
     * 对应数据库字段：publish_time
     */
    @TableField("publish_time")
    private String publishTime;

    /**
     * 分类路径
     * 对应数据库字段：category_path
     */
    @TableField("category_path")
    private String categoryPath;

    /**
     * 成交时间
     * 对应数据库字段：sold_at
     */
    @TableField("sold_at")
    private LocalDateTime soldAt;

    /**
     * 上架时间
     * 对应数据库字段：created_at
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 对应数据库字段：updated_at
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除时间（软删除）
     * 对应数据库字段：deleted_at
     */
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
