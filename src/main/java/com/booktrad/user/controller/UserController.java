package com.booktrad.user.controller;

import com.booktrad.common.result.Result;
import com.booktrad.user.dto.LoginDTO;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;
import com.booktrad.user.service.UserService;
import com.booktrad.user.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
}
