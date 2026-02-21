package com.booktrad.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.dto.BookPublishDTO;
import com.booktrad.book.dto.BookQueryDTO;
import com.booktrad.book.dto.BookUpdateDTO;
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
     * @param bookQueryDTO 查询条件DTO
     * @return 分页结果，包含records、total、current、size
     */
    IPage<BookPageVO> getBookPage(BookQueryDTO bookQueryDTO);

    /**
     * 发布书籍
     * @param bookPublishDTO 书籍发布请求DTO
     * @param sellerId 卖家用户ID（从当前登录用户中获取）
     * @return 发布结果，包含书籍ID
     */
    BookPublishVO publishBook(BookPublishDTO bookPublishDTO, Long sellerId);

    /**
     * 更新书籍信息
     * @param bookUpdateDTO 书籍更新DTO
     * @return 更新后的书籍详情
     */
    BookVO updateBook(BookUpdateDTO bookUpdateDTO);

    /**
     * 卖家下架书籍
     * @param bookId 书籍ID
     * @return 下架后的书籍详情
     */
    BookVO offShelfBook(Long bookId);

    /**
     * 分页查询卖家的所有书籍（包括已下架、已封禁的）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    IPage<BookPageVO> getSellerBookPage(Integer page, Integer size);

    /**
     * 管理员获取书籍列表
     * @param title 书名（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 书籍分页列表
     */
    IPage<BookPageVO> getAdminBookList(String title, Integer pageNum, Integer pageSize);

    /**
     * 管理员删除书籍（逻辑删除）
     * @param bookId 书籍ID
     */
    void adminDeleteBook(Long bookId);

    /**
     * 管理员封禁书籍
     * @param bookId 书籍ID
     * @param reason 封禁原因
     */
    void banBook(Long bookId, String reason);

    /**
     * 管理员解封书籍
     * @param bookId 书籍ID
     */
    void unbanBook(Long bookId);


    /**
     * 获取相关推荐书籍（看过这本书的人也看了）
     * 优先推荐同类别的书籍，不够则随机补充其他书籍
     * @param bookId 当前书籍ID
     * @param limit 推荐数量，默认6本
     * @return 推荐书籍列表
     */
    java.util.List<BookPageVO> getRelatedBooks(Long bookId, Integer limit);

}
