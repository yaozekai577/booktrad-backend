package com.booktrad.common.context;

/**
 * 用户上下文工具类
 * 用于存储和获取当前登录用户信息到ThreadLocal中
 *
 * @author 
 * @since 2025-12-16
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
