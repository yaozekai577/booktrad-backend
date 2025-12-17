package com.booktrad.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;

/**
 * 用户Service接口
 *
 * @author 
 * @since 2025-12-16
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
