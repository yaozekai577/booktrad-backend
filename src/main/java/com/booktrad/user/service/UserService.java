package com.booktrad.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户Service接口，定义用户相关业务逻辑
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    User register(RegisterDTO registerDTO);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
    
    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT token
     */
    String generateToken(Long userId, Integer role);
}
