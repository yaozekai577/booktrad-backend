package com.booktrad.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.user.entity.User;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码（已加密）
     * @return 影响行数
     */
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    /**
     * 更新用户作为卖家的评分统计
     * @param userId 用户ID
     * @param newScore 新评分
     * @param newCount 新评价次数
     * @return 影响行数
     */
    int updateSellerRating(@Param("userId") Long userId, 
                          @Param("newScore") Double newScore, 
                          @Param("newCount") Integer newCount);

    /**
     * 更新用户作为买家的评分统计
     * @param userId 用户ID
     * @param newScore 新评分
     * @param newCount 新评价次数
     * @return 影响行数
     */
    int updateBuyerRating(@Param("userId") Long userId, 
                         @Param("newScore") Double newScore, 
                         @Param("newCount") Integer newCount);

    /**
     * 根据用户ID查询用户（用于评价系统）
     * @param userId 用户ID
     * @return 用户信息
     */
    User selectUserById(@Param("userId") Long userId);

    /**
     * 查询卖家在售的书籍列表
     * @param sellerId 卖家ID
     * @return 在售书籍列表
     */
    List<Map<String, Object>> selectSellerOnSaleBooks(@Param("sellerId") Long sellerId);

    /**
     * 查询卖家收到的评价列表
     * @param sellerId 卖家ID
     * @return 评价列表
     */
    List<Map<String, Object>> selectSellerReviews(@Param("sellerId") Long sellerId);

    /**
     * 统计卖家收到的评价数量
     * @param sellerId 卖家ID
     * @return 评价数量
     */
    Integer countSellerReviews(@Param("sellerId") Long sellerId);

    /**
     * 管理员分页查询用户
     * @param page 分页对象
     * @param username 用户名（可选，模糊查询）
     * @return 用户列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<User> selectUserPage(
        com.baomidou.mybatisplus.core.metadata.IPage<User> page, 
        @Param("username") String username
    );

    /**
     * 根据ID查询用户实体（手写SQL）
     * @param id 用户ID
     * @return 用户实体
     */
    User selectUserEntityById(@Param("id") Long id);

    /**
     * 更新用户封禁状态
     * @param userId 用户ID
     * @param status 状态：0-封禁，1-正常
     * @param banReason 封禁原因
     */
    void updateUserBanStatus(@Param("userId") Long userId, @Param("status") Integer status, @Param("banReason") String banReason);

    /**
     * 更新用户信息（邮箱和电话）
     * @param userId 用户ID
     * @param email 邮箱地址
     * @param phone 电话号码
     * @return 影响行数
     */
    int updateUserInfo(@Param("userId") Long userId, @Param("email") String email, @Param("phone") String phone);

    /**
     * 统计每日新增用户（过去N天）
     * @param days 天数
     * @return 日期和数量的列表
     */
    List<Map<String, Object>> selectDailyNewUsers(@Param("days") Integer days);
    /**
     * 统计总用户数
     * @return 用户总数
     */
    Integer countTotalUsers();
}
