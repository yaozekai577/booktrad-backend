package com.booktrad.book.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 查询书籍请求DTO，用于接收查询书籍的请求参数
 * @Date 2025/12/24 22:00
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class BookQueryDTO {

    /**
     * 书名关键字（模糊查询）
     */
    private String title;

    /**
     * 作者关键字（模糊查询）
     */
    private String author;

    /**
     * 搜索关键字（模糊查询书名或作者）
     */
    private String keyword;

    /**
     * ISBN编号（精确查询）
     */
    private String isbn;

    /**
     * 书籍分类ID（精确查询）
     */
    private Long categoryId;

    /**
     * 最小价格（价格区间查询）
     */
    private BigDecimal minPrice;

    /**
     * 最大价格（价格区间查询）
     */
    private BigDecimal maxPrice;

    /**
     * 成色列表，支持多选：1-全新 2-九成新 3-八成新 4-明显使用痕迹
     */
    private List<Integer> bookConditions;

    /**
     * 状态：1-在售 2-已下架 3-已售出（精确查询）
     */
    private Integer status;

    /**
     * 交易方式：自取/邮寄（精确查询）
     */
    private String tradeType;

    /**
     * 出版时间（精确查询，格式：yyyy-MM）
     */
    private String publishTime;

    /**
     * 分类路径（模糊查询）
     */
    private String categoryPath;

    /**
     * 是否封禁：0-正常 1-封禁（精确查询）
     */
    private Integer isBanned;

    /**
     * 排序字段，例如：price、createdAt、viewCount
     */
    private String sortField;

    /**
     * 排序方式：asc-升序 desc-降序
     */
    private String sortOrder;

    /**
     * 页码，从1开始
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer size;
}