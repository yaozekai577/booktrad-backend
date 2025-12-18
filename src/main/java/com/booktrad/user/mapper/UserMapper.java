package com.booktrad.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.user.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 * 继承BaseMapper，使用MyBatis Plus的CRUD方法，复杂查询使用XML配置
 *
 * @author 
 * @since 2025-12-16
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
}
