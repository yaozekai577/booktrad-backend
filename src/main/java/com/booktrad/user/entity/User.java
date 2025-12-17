package com.booktrad.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author 
 * @since 2025-12-16
 */
@Data
@TableName("user")
public class User {

    /**
     * 用户ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名，用于登录
     */
    private String username;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 角色：0-普通用户，1-管理员
     */
    private Integer role;

    /**
     * 状态：0-封禁，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除时间，用于软删除
     */
    @TableLogic
    private LocalDateTime deletedAt;

    /**
     * 扩展信息，用于存储额外用户信息，如头像、联系方式、学院、年级等
     */
    private JsonNode extraInfo;
}
