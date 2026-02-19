package com.booktrad.user.controller;

import com.booktrad.common.context.UserContext;
import com.booktrad.common.result.Result;
import com.booktrad.user.dto.ChangePasswordDTO;
import com.booktrad.user.dto.LoginDTO;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;
import com.booktrad.user.service.UserService;
import com.booktrad.user.vo.LoginVO;
import com.booktrad.user.vo.SellerProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户控制器，处理用户登录和注册相关的HTTP请求
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginDTO 登录请求DTO，包含用户名和密码
     * @return 统一返回格式，包含登录成功的用户信息和JWT token
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        try {
            // 调用登录Service，处理登录逻辑
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            
            // 生成JWT token
            String token = userService.generateToken(user.getId(), user.getRole());
            
            // 构建登录响应VO
            LoginVO loginVO = new LoginVO();
            loginVO.setId(user.getId());
            loginVO.setUsername(user.getUsername());
            loginVO.setEmail(user.getEmail());
            loginVO.setPhone(user.getPhone());
            loginVO.setRole(user.getRole());
            loginVO.setStatus(user.getStatus());
            loginVO.setCreatedAt(user.getCreatedAt());
            loginVO.setUpdatedAt(user.getUpdatedAt());
            loginVO.setExtraInfo(user.getExtraInfo());
            loginVO.setToken(token);
            
            // 返回成功响应，包含登录信息和token
            return Result.success(loginVO);
        } catch (RuntimeException e) {
            // 登录失败，返回错误信息
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册接口
     * @param registerDTO 注册请求DTO，包含注册所需的所有信息
     * @return 统一返回格式，包含注册成功的用户信息和JWT token
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 调用注册Service，处理注册逻辑
            User user = userService.register(registerDTO);
            
            // 生成JWT token
            String token = userService.generateToken(user.getId(), user.getRole());
            
            // 构建登录响应VO（注册成功后直接返回登录信息，方便前端直接登录）
            LoginVO loginVO = new LoginVO();
            loginVO.setId(user.getId());
            loginVO.setUsername(user.getUsername());
            loginVO.setEmail(user.getEmail());
            loginVO.setPhone(user.getPhone());
            loginVO.setRole(user.getRole());
            loginVO.setStatus(user.getStatus());
            loginVO.setCreatedAt(user.getCreatedAt());
            loginVO.setUpdatedAt(user.getUpdatedAt());
            loginVO.setExtraInfo(user.getExtraInfo());
            loginVO.setToken(token);
            
            // 返回成功响应，包含注册信息和token
            return Result.success(loginVO);
        } catch (RuntimeException e) {
            // 注册失败，返回错误信息
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码接口
     * @param changePasswordDTO 修改密码DTO，包含旧密码、新密码和确认密码
     * @return 统一返回格式
     */
    @PutMapping("/change-password")
    public Result<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            // 从上下文中获取当前登录用户ID
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error(401, "用户未登录");
            }

            // 调用Service层修改密码
            userService.changePassword(userId, changePasswordDTO);

            // 返回成功响应
            return Result.success("密码修改成功");
        } catch (RuntimeException e) {
            // 修改失败，返回错误信息
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取卖家主页信息
     * @param sellerId 卖家ID
     * @return 卖家主页信息，包含卖家基本信息、评价列表和在售书籍列表
     */
    @GetMapping("/seller/{sellerId}")
    public Result<SellerProfileVO> getSellerProfile(@PathVariable Long sellerId) {
        try {
            // 调用Service层获取卖家主页信息
            SellerProfileVO sellerProfile = userService.getSellerProfile(sellerId);
            
            // 返回成功响应
            return Result.success(sellerProfile);
        } catch (RuntimeException e) {
            // 查询失败，返回错误信息
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员获取用户列表
     * @param username 用户名（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    @GetMapping("/admin/users")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<User>> getUserList(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String username,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") Integer pageNum,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // TODO: 可以在这里添加权限校验，确保当前用户是管理员
            // Long currentUserId = UserContext.getUserId();
            // User currentUser = userService.getById(currentUserId);
            // if (currentUser == null || currentUser.getRole() != 1) {
            //     return Result.error(403, "无权访问");
            // }

            return Result.success(userService.getUserList(username, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 管理员更新用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 成功信息
     */
    @PutMapping("/admin/users/{userId}/status")
    public Result<String> updateUserStatus(
            @PathVariable Long userId,
            @org.springframework.web.bind.annotation.RequestParam Integer status) {
        try {
            // TODO: 权限校验

            userService.updateUserStatus(userId, status);
            return Result.success("更新状态成功");
        } catch (Exception e) {
            return Result.error("更新状态失败：" + e.getMessage());
        }
    }
}
