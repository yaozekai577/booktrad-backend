package com.booktrad.user.vo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应VO
 * 用于返回登录成功后的用户信息，不包含密码
 *
 * @author 
 * @since 2025-12-16
 */
@Data
public class LoginVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

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
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 扩展信息，用于存储额外用户信息，如头像、联系方式、学院、年级等
     */
    private JsonNode extraInfo;

    /**
     * JWT令牌，用于后续请求的身份验证
     */
    private String token;
}
