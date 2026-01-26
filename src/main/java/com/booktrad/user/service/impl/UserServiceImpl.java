package com.booktrad.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.booktrad.common.utils.JwtUtil;
import com.booktrad.user.dto.ChangePasswordDTO;
import com.booktrad.user.dto.RegisterDTO;
import com.booktrad.user.entity.User;
import com.booktrad.user.mapper.UserMapper;
import com.booktrad.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户Service实现类 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public User getByUsername(String username) {
        log.info("开始查询用户，用户名: '{}'", username);
        
        // 使用自定义查询方法
        User user = userMapper.selectByUsername(username);
        log.info("自定义查询结果: {}", user);
        
        // 查询所有用户（用于调试）
        List<User> allUsers = userMapper.selectAllUsers();
        log.info("所有用户: {}", allUsers);
        
        return user;
    }

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT token
     */
    @Override
    public String generateToken(Long userId, Integer role) {
        // 使用JwtUtil工具类生成token
        return JwtUtil.generateToken(userId, role);
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     */
    @Override
    public User login(String username, String password) {
        // 1. 根据用户名查询用户
        User user = getByUsername(username);
        
        // 2. 用户不存在则返回错误
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        
        // 3. 用户被封禁(status=0)则拒绝登录
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被封禁");
        }
        
        // 4. 使用BCrypt校验密码
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 5. 登录成功，这里不直接返回token，token会在Controller层生成
        return user;
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    @Override
    public User register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        User existingUser = getByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 2. 验证密码和确认密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 3. 对密码进行BCrypt加密
        String encryptedPassword = bCryptPasswordEncoder.encode(registerDTO.getPassword());
        
        // 4. 创建User对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptedPassword);
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setRole(0); // 默认角色：0-普通用户
        user.setStatus(1); // 默认状态：1-正常
        
        // 5. 保存用户到数据库
        userMapper.insert(user);
        
        // 6. 返回注册成功的用户信息（不包含密码）
        return user;
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param changePasswordDTO 修改密码DTO
     */
    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        // 1. 参数校验
        if (changePasswordDTO.getOldPassword() == null || changePasswordDTO.getOldPassword().trim().isEmpty()) {
            throw new RuntimeException("旧密码不能为空");
        }
        if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("新密码不能为空");
        }
        if (changePasswordDTO.getConfirmPassword() == null || changePasswordDTO.getConfirmPassword().trim().isEmpty()) {
            throw new RuntimeException("确认密码不能为空");
        }

        // 2. 验证新密码和确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }

        // 3. 验证新密码不能与旧密码相同
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 4. 查询用户信息
        User user = userMapper.selectUserForOrder(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 5. 验证旧密码是否正确
        if (!bCryptPasswordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 6. 加密新密码
        String encryptedNewPassword = bCryptPasswordEncoder.encode(changePasswordDTO.getNewPassword());

        // 7. 更新密码
        int result = userMapper.updatePassword(userId, encryptedNewPassword);
        if (result <= 0) {
            throw new RuntimeException("修改密码失败");
        }

        log.info("用户 {} 修改密码成功", userId);
    }
}
