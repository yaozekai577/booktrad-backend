package com.booktrad.user.dto;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 更新用户信息请求DTO，用于接收前端提交的用户信息修改数据
 * @Date 2026-01-04
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public class UpdateUserInfoDTO {
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 电话号码
     */
    private String phone;

    // getter和setter方法
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}