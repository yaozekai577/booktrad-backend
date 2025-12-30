package com.booktrad.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.dto.BookPublishDTO;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookPublishVO;
import com.booktrad.book.vo.BookVO;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍服务接口
 * @Date 2025/12/29 21:22
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface BookService {

    /**
     * 根据ID获取书籍详情
     * @param id 书籍ID
     * @return 书籍详情VO
     */
    BookVO getBookDetail(Long id);

    /**
     * 首页书籍分页查询
     * 只查询"在售、未封禁、未删除"的书籍
     * @param current 当前页
     * @param size 每页条数
     * @param keyword 搜索关键词（匹配书名或作者，可选）
     * @param categoryId 分类ID（可选）
     * @return 分页结果，包含records、total、current、size
     */
    IPage<BookPageVO> getBookPage(Integer current, Integer size, String keyword, Long categoryId);

    /**
     * 发布书籍
     * @param bookPublishDTO 书籍发布请求DTO
     * @param sellerId 卖家用户ID（从当前登录用户中获取）
     * @return 发布结果，包含书籍ID
     */
    BookPublishVO publishBook(BookPublishDTO bookPublishDTO, Long sellerId);
}
