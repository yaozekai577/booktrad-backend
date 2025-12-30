package com.booktrad.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.entity.Book;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍Mapper接口
 * @Date 2025/12/29 21:30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 根据ID查询书籍详情
     * @param id 书籍ID
     * @return 书籍详情VO
     */
    BookVO selectBookDetailById(Long id);

    /**
     * 首页书籍分页查询
     * 只查询"在售、未封禁、未删除"的书籍
     * @param page 分页对象
     * @param keyword 搜索关键词（匹配书名或作者，可选）
     * @param categoryId 分类ID（可选）
     * @return 分页结果
     */
    IPage<BookPageVO> selectBookPage(IPage<BookPageVO> page, @Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}