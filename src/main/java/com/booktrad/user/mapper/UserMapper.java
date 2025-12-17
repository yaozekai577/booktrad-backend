package com.booktrad.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.user.entity.User;

/**
 * 用户Mapper接口
 * 继承BaseMapper，使用MyBatis Plus的CRUD方法，不需要XML配置
 *
 * @author 
 * @since 2025-12-16
 */
public interface UserMapper extends BaseMapper<User> {
}
