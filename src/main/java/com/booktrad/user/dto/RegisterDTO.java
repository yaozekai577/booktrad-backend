package com.booktrad.user.dto;

import lombok.Data;

/**
 * 注册请求DTO
 * 用于接收注册接口的请求参数
 *
 * @author 
 * @since 2025-12-17
 */
@Data
public class RegisterDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;
}