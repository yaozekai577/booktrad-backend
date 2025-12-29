package com.booktrad.book.controller;

import com.booktrad.book.service.BookService;
import com.booktrad.book.vo.BookVO;
import com.booktrad.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 项目名称：booktrad
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
}
