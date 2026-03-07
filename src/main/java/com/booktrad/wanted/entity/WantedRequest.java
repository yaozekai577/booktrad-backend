package com.booktrad.wanted.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wanted_request")
public class WantedRequest {

    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    @TableField("buyer_id")
    private Long buyerId;

    @TableField("title")
    private String title;

    @TableField("author")
    private String author;

    @TableField("category_id")
    private Long categoryId;

    @TableField("budget")
    private BigDecimal budget;

    @TableField("desired_condition")
    private String desiredCondition;

    @TableField("expected_location")
    private String expectedLocation;

    @TableField("description")
    private String description;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private Integer status;

    @TableField("close_reason")
    private String closeReason;

    @TableField("closed_at")
    private LocalDateTime closedAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}
