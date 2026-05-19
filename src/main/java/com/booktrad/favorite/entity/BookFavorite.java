package com.booktrad.favorite.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍收藏实体类
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@Data
@TableName("book_favorite")
public class BookFavorite {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    /**
     * 用户ID（关联 user 表）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 书籍ID（关联 book 表）
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 状态：1-已收藏 0-已取消
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}