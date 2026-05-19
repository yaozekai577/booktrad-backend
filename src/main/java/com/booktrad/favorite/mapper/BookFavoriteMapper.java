package com.booktrad.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.favorite.entity.BookFavorite;
import com.booktrad.favorite.vo.FavoriteBookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍收藏Mapper接口
 * @Date 2026/05/19
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@Mapper
public interface BookFavoriteMapper extends BaseMapper<BookFavorite> {

    /**
     * 根据用户ID和书籍ID查询收藏记录
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 收藏记录（可能为null）
     */
    BookFavorite selectByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 分页查询用户的收藏书籍列表（关联book表获取书籍信息）
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果，包含书籍基本信息
     */
    IPage<FavoriteBookVO> selectFavoriteBookPage(IPage<FavoriteBookVO> page, @Param("userId") Long userId);

    /**
     * 统计用户的收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    Integer countUserFavorites(@Param("userId") Long userId);

    /**
     * 切换收藏状态：如果记录存在则更新status，不存在则插入
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @param status 状态：1-已收藏 0-已取消
     * @return 影响行数
     */
    int upsertFavorite(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("status") Integer status);
}