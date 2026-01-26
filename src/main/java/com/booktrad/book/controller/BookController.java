package com.booktrad.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.dto.BookPublishDTO;
import com.booktrad.book.dto.BookQueryDTO;
import com.booktrad.book.dto.BookUpdateDTO;
import com.booktrad.book.service.BookService;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookPublishVO;
import com.booktrad.book.vo.BookVO;
import com.booktrad.common.context.UserContext;
import com.booktrad.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名称：booktrading
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍控制器
 * @Date 2025/12/29 21:12
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 首页书籍分页查询
     * 只查询"在售、未封禁、未删除"的书籍
     * 支持高级筛选：关键词、分类、成色多选、价格区间
     * @param bookQueryDTO 查询条件DTO
     * @return 分页结果，包含records、total、current、size
     */
    @GetMapping("/page")
    public Result<IPage<BookPageVO>> getBookPage(BookQueryDTO bookQueryDTO) {
        try {
            // 设置当前登录用户ID，用于排序优化
            Long currentUserId = UserContext.getUserId();
            if (currentUserId != null) {
                bookQueryDTO.setCurrentUserId(currentUserId);
            }
            
            IPage<BookPageVO> bookPage = bookService.getBookPage(bookQueryDTO);
            return Result.success(bookPage);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布书籍
     * @param bookPublishDTO 书籍发布请求DTO
     * @return 发布结果，包含书籍ID
     */
    @PostMapping("/publish")
    public Result<BookPublishVO> publishBook(@RequestBody BookPublishDTO bookPublishDTO) {
        try {
            // 从当前登录用户中获取sellerId
            Long sellerId = UserContext.getUserId();
            if (sellerId == null) {
                return Result.error(401, "未授权，请先登录");
            }

            // 调用Service层发布书籍
            BookPublishVO bookPublishVO = bookService.publishBook(bookPublishDTO, sellerId);
            return Result.success("发布成功", bookPublishVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取书籍详情
     * @param id 书籍ID
     * @return 书籍详情
     */
    @GetMapping("/{id}")
    public Result<BookVO> getBookDetail(@PathVariable Long id) {
        try {
            BookVO bookVO = bookService.getBookDetail(id);
            return Result.success(bookVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新书籍信息
     * @param bookUpdateDTO 书籍更新DTO
     * @return 更新后的书籍详情
     */
    @PutMapping("/update")
    public Result<BookVO> updateBook(@RequestBody BookUpdateDTO bookUpdateDTO) {
        try {
            BookVO bookVO = bookService.updateBook(bookUpdateDTO);
            return Result.success("更新成功", bookVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
