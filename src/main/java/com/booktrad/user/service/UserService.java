package com.booktrad.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.booktrad.user.dto.ChangePasswordDTO;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;
import com.booktrad.user.vo.SellerProfileVO;

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

    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码DTO
     */
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    /**
     * 获取卖家主页信息
     * @param sellerId 卖家ID
     * @return 卖家主页信息，包含卖家基本信息、评价列表和在售书籍列表
     */
    SellerProfileVO getSellerProfile(Long sellerId);

    /**
     * 管理员获取用户列表
     * @param username 用户名（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户分页列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<User> getUserList(String username, Integer pageNum, Integer pageSize);

    /**
     * 管理员更新用户状态
     * @param userId 用户ID
     * @param status 状态：0-封禁，1-正常
     */
    void updateUserStatus(Long userId, Integer status);
}
