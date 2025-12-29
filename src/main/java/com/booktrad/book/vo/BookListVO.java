package com.booktrad.book.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍列表VO，用于返回书籍列表信息
 * @Date 2025/12/24 22:00
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookListVO {

    /**
     * 书籍ID
     */
    private Long id;

    /**
     * 卖家用户ID
     */
    private Long sellerId;

    /**
     * 卖家用户名
     */
    private String sellerUsername;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * ISBN编号
     */
    private String isbn;

    /**
     * 书籍分类名称
     */
    private String categoryName;

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
     * 成色描述
     */
    private String bookConditionDesc;

    /**
     * 封面图片URL字符串（数据库存储，以逗号分隔）
     * 仅用于接收数据库查询结果，不对外暴露
     */
    private String coverImageStr;

    /**
     * 封面图片URL列表
     */
    private List<String> coverImage;

    /**
     * 状态：1-在售 2-已下架 3-已售出
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 交易方式：自取/邮寄
     */
    private String tradeType;

    /**
     * 出版时间 yyyy-MM
     */
    private String publishTime;

    /**
     * 是否封禁：0-正常 1-封禁
     */
    private Integer isBanned;

    /**
     * 是否封禁描述
     */
    private String isBannedDesc;

    /**
     * 上架时间
     */
    private LocalDateTime createdAt;
}