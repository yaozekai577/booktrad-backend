package com.booktrad.user.dto;

import lombok.Data;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 修改密码DTO
 * @Date 2026/1/26
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class ChangePasswordDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmPassword;
}
