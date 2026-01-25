package com.booktrad.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.user.entity.User;

import java.util.List;
import java.util.Map;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户Mapper接口，继承BaseMapper，使用MyBatis Plus的CRUD方法，复杂查询使用XML配置 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 自定义查询方法，根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(String username);
    
    /**
     * 查询所有用户（用于调试）
     * @return 所有用户列表
     */
    List<User> selectAllUsers();
    
    /**
     * 动态条件查询用户
     * @param condition 查询条件
     * @return 用户列表
     */
    List<User> selectUsersByCondition(Map<String, Object> condition);
    
    /**
     * 根据ID查询用户（用于订单创建，忽略逻辑删除）
     * @param id 用户ID
     * @return 用户信息
     */
    User selectUserForOrder(Long id);
}
