package com.booktrad.common.context;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  用户上下文工具类，用于存储和获取当前登录用户信息到ThreadLocal中 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
public class UserContext {
    
    // 线程本地变量，用于存储当前登录用户ID
    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();
    
    // 线程本地变量，用于存储当前登录用户角色
    private static final ThreadLocal<Integer> userRoleThreadLocal = new ThreadLocal<>();

    /**
     * 设置当前登录用户ID
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        userIdThreadLocal.set(userId);
    }

    /**
     * 获取当前登录用户ID
     * @return 用户ID
     */
    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    /**
     * 设置当前登录用户角色
     * @param role 用户角色
     */
    public static void setUserRole(Integer role) {
        userRoleThreadLocal.set(role);
    }

    /**
     * 获取当前登录用户角色
     * @return 用户角色
     */
    public static Integer getUserRole() {
        return userRoleThreadLocal.get();
    }

    /**
     * 清除当前线程的用户信息
     * 防止内存泄漏
     */
    public static void clear() {
        userIdThreadLocal.remove();
        userRoleThreadLocal.remove();
    }
}
