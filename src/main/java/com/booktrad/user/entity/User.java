package com.booktrad.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户实体类，存储用户基本信息
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@Data
@TableName("user")
public class User {

    /**
     * 用户ID，主键，自增
     * 对应数据库字段：id
     */
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;

    /**
     * 用户名，用于登录
     * 对应数据库字段：username
     */
    @TableField("username")
    private String username;

    /**
     * 密码，加密存储
     * 对应数据库字段：password
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     * 对应数据库字段：email
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     * 对应数据库字段：phone
     */
    @TableField("phone")
    private String phone;

    /**
     * 角色：0-普通用户，1-管理员
     * 对应数据库字段：role
     */
    @TableField("role")
    private Integer role;

    /**
     * 状态：0-封禁，1-正常
     * 对应数据库字段：status
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
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
     * 删除时间，用于软删除
     * 对应数据库字段：deleted_at
     */
    @TableLogic
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 扩展信息，用于存储额外用户信息，如头像、联系方式、学院、年级等
     * 对应数据库字段：extra_info
     */
    @TableField("extra_info")
    private JsonNode extraInfo;
}
