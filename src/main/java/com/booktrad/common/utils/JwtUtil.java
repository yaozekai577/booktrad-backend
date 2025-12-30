package com.booktrad.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  JWT工具类，用于生成和解析JWT token，使用HS256算法 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
public class JwtUtil {

    // JWT密钥，实际项目中应配置在application.properties或application.yaml中
    private static final String JWT_SECRET = "booktrading_jwt_secret_key_20251216";
    
    // 默认过期时间，48小时（毫秒）
    private static final long DEFAULT_EXPIRATION = 1728000000;

    /**
     * 生成JWT token
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT token
     */
    public static String generateToken(Long userId, Integer role) {
        return generateToken(userId, role, DEFAULT_EXPIRATION);
    }

    /**
     * 生成JWT token，支持自定义过期时间
     * @param userId 用户ID
     * @param role 用户角色
     * @param expiration 过期时间（毫秒）
     * @return JWT token
     */
    public static String generateToken(Long userId, Integer role, long expiration) {
        // 创建密钥
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        
        // 当前时间
        Date now = new Date();
        // 过期时间
        Date expiryDate = new Date(now.getTime() + expiration);
        
        // 生成token，使用JJWT 0.12.5正确的API
        return Jwts
                .builder()
                // 设置userId
                .claim("userId", userId)
                // 设置role
                .claim("role", role)
                // 设置签发时间
                .issuedAt(now)
                // 设置过期时间
                .expiration(expiryDate)
                // 签名
                .signWith(key)
                // 压缩
                .compact();
    }

    /**
     * 解析JWT token
     * @param token JWT token
     * @return Claims对象，包含token中的信息
     */
    public static Claims parseToken(String token) {
        // 创建密钥
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
        
        // 解析token，使用JJWT 0.12.5正确的API
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取用户角色
     * @param token JWT token
     * @return 用户角色
     */
    public static Integer getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", Integer.class);
    }

    /**
     * 判断token是否过期
     * @param token JWT token
     * @return true表示过期，false表示未过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
