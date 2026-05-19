package com.booktrad.favorite.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.booktrad.favorite.entity.BookFavorite;
import com.booktrad.favorite.mapper.BookFavoriteMapper;
import com.booktrad.favorite.service.BookFavoriteService;
import com.booktrad.favorite.vo.FavoriteBookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍收藏服务实现类
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@Service
public class BookFavoriteServiceImpl implements BookFavoriteService {

    @Autowired
    private BookFavoriteMapper bookFavoriteMapper;

    @Override
    public boolean toggleFavorite(Long userId, Long bookId) {
        // 查询当前收藏状态
        BookFavorite existing = bookFavoriteMapper.selectByUserIdAndBookId(userId, bookId);

        if (existing == null) {
            // 没有记录，插入新记录，状态设为已收藏
            bookFavoriteMapper.upsertFavorite(userId, bookId, 1);
            return true;
        } else {
            // 已有记录，切换状态
            int newStatus = (existing.getStatus() == 1) ? 0 : 1;
            bookFavoriteMapper.upsertFavorite(userId, bookId, newStatus);
            return newStatus == 1;
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long bookId) {
        BookFavorite favorite = bookFavoriteMapper.selectByUserIdAndBookId(userId, bookId);
        return favorite != null && favorite.getStatus() == 1;
    }

    @Override
    public IPage<FavoriteBookVO> getFavoriteBooks(Long userId, Integer page, Integer size) {
        // 处理分页参数默认值
        int pageNum = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        Page<FavoriteBookVO> pageObj = new Page<>(pageNum, pageSize);
        return bookFavoriteMapper.selectFavoriteBookPage(pageObj, userId);
    }

    @Override
    public Integer getFavoriteCount(Long userId) {
        return bookFavoriteMapper.countUserFavorites(userId);
    }
}