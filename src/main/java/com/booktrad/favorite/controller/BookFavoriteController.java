package com.booktrad.favorite.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.common.context.UserContext;
import com.booktrad.common.result.Result;
import com.booktrad.favorite.service.BookFavoriteService;
import com.booktrad.favorite.vo.FavoriteBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍收藏控制器
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api/favorite")
public class BookFavoriteController {

    @Autowired
    private BookFavoriteService bookFavoriteService;

    /**
     * 切换收藏状态（收藏/取消收藏）
     * @param bookId 书籍ID
     * @return 返回切换后的收藏状态
     */
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggleFavorite(@RequestParam Long bookId) {
        try {
            // 从当前登录用户中获取userId
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error(401, "请先登录");
            }

            boolean isFavorited = bookFavoriteService.toggleFavorite(userId, bookId);

            Map<String, Object> data = new HashMap<>();
            data.put("isFavorited", isFavorited);
            data.put("bookId", bookId);
            data.put("message", isFavorited ? "收藏成功" : "已取消收藏");

            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询某本书的收藏状态
     * @param bookId 书籍ID
     * @return 返回该书的收藏状态
     */
    @GetMapping("/status/{bookId}")
    public Result<Map<String, Object>> getFavoriteStatus(@PathVariable Long bookId) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error(401, "请先登录");
            }

            boolean isFavorited = bookFavoriteService.isFavorited(userId, bookId);

            Map<String, Object> data = new HashMap<>();
            data.put("isFavorited", isFavorited);
            data.put("bookId", bookId);

            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的收藏书籍列表（分页）
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @return 分页结果
     */
    @GetMapping("/my")
    public Result<IPage<FavoriteBookVO>> getMyFavorites(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error(401, "请先登录");
            }

            IPage<FavoriteBookVO> favorites = bookFavoriteService.getFavoriteBooks(userId, page, size);
            return Result.success(favorites);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的收藏数量
     * @return 收藏数量
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getFavoriteCount() {
        try {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                return Result.error(401, "请先登录");
            }

            Integer count = bookFavoriteService.getFavoriteCount(userId);

            Map<String, Object> data = new HashMap<>();
            data.put("count", count);

            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}