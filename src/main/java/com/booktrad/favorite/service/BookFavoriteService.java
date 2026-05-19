package com.booktrad.favorite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.favorite.vo.FavoriteBookVO;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍收藏服务接口
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
public interface BookFavoriteService {

    /**
     * 切换收藏状态（收藏/取消收藏）
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 当前收藏状态：true-已收藏 false-已取消
     */
    boolean toggleFavorite(Long userId, Long bookId);

    /**
     * 判断某本书是否已被用户收藏
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return true-已收藏 false-未收藏
     */
    boolean isFavorited(Long userId, Long bookId);

    /**
     * 分页查询用户的收藏书籍列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果，包含收藏的书籍信息
     */
    IPage<FavoriteBookVO> getFavoriteBooks(Long userId, Integer page, Integer size);

    /**
     * 统计用户的收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    Integer getFavoriteCount(Long userId);
}