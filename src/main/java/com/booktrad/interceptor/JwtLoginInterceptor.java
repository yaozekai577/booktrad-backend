package com.booktrad.interceptor;

import com.booktrad.common.context.UserContext;
import com.booktrad.common.result.Result;
import com.booktrad.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT登录拦截器
 * 用于拦截除登录接口外的所有请求，校验JWT token的合法性
 *
 * @author 
 * @since 2025-12-16
 */
@Component
public class JwtLoginInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前执行
     * 用于校验JWT token的合法性
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return true表示继续执行，false表示中断执行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 检查请求路径是否为登录接口，登录接口直接放行
        String requestURI = request.getRequestURI();
        if ("/api/auth/login".equals(requestURI)) {
            return true;
        }

        // 2. 从Authorization Header中获取token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 没有Authorization Header或格式不正确，返回未授权错误
            returnJson(response, Result.error(401, "未授权，请先登录"));
            return false;
        }

        // 3. 提取token（去掉"Bearer "前缀）
        String token = authorizationHeader.substring(7);

        try {
            // 4. 解析和校验token
            Claims claims = JwtUtil.parseToken(token);
            
            // 5. 从token中获取用户ID和角色
            Long userId = claims.get("userId", Long.class);
            Integer role = claims.get("role", Integer.class);
            
            // 6. 将用户信息存入ThreadLocal
            UserContext.setUserId(userId);
            UserContext.setUserRole(role);
            
            // 7. token校验成功，继续执行
            return true;
        } catch (ExpiredJwtException e) {
            // token过期
            returnJson(response, Result.error(401, "登录已过期，请重新登录"));
        } catch (MalformedJwtException e) {
            // token格式错误
            returnJson(response, Result.error(401, "无效的登录凭证"));
        } catch (JwtException e) {
            // 其他JWT异常
            returnJson(response, Result.error(401, "登录凭证无效，请重新登录"));
        } catch (Exception e) {
            // 其他异常
            returnJson(response, Result.error(500, "服务器内部错误"));
        }
        
        // 8. token校验失败，中断执行
        return false;
    }

    /**
     * 请求处理后执行
     * 用于清除ThreadLocal中的用户信息，防止内存泄漏
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param ex 异常
     * @throws Exception 异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的用户信息，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 将结果转换为JSON格式并写入响应
     * @param response HTTP响应
     * @param result 响应结果
     * @throws IOException IO异常
     */
    private void returnJson(HttpServletResponse response, Result<?> result) throws IOException {
        // 设置响应类型为JSON
        response.setContentType("application/json;charset=UTF-8");
        // 设置响应状态码
        response.setStatus(200);
        // 将结果转换为JSON字符串
        String jsonResult = "{\"code\":" + result.getCode() + ",\"msg\":\"" + result.getMsg() + "\",\"data\":" + (result.getData() == null ? "null" : "{}") + "}";
        // 写入响应
        PrintWriter writer = response.getWriter();
        writer.write(jsonResult);
        writer.flush();
        writer.close();
    }
}
