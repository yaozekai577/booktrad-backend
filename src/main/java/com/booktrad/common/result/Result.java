package com.booktrad.common.result;

import lombok.Data;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  统一响应结果封装，用于所有API接口的响应格式统一 
 * @Date 2025-12-18 21:00:00 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@Data
public class Result<T> {

    /**
     * 响应码：1成功，0失败，其他业务错误码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造方法
     */
    private Result() {
    }

    /**
     * 成功响应，返回默认消息
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMsg("操作成功");
        return result;
    }

    /**
     * 成功响应，返回指定数据
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功响应，返回指定消息和数据
     * @param msg 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应，返回默认消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg("操作失败");
        return result;
    }

    /**
     * 失败响应，返回指定消息
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败响应，返回指定响应码和消息
     * @param code 响应码
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
