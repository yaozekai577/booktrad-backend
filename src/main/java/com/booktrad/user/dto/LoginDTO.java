package com.booktrad.user.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 用于接收登录接口的请求参数
 *
 * @author 
 * @since 2025-12-16
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
