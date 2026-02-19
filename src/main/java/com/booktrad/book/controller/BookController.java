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
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 卖家下架书籍
     * @param id 书籍ID
     * @return 下架后的书籍详情
     */
    @PutMapping("/off-shelf/{id}")
    public Result<BookVO> offShelfBook(@PathVariable Long id) {
        try {
            BookVO bookVO = bookService.offShelfBook(id);
            return Result.success("下架成功", bookVO);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询卖家的所有书籍（包括已下架、已封禁的）
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    @GetMapping("/my-books")
    public Result<IPage<BookPageVO>> getSellerBookPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        try {
            IPage<BookPageVO> bookPage = bookService.getSellerBookPage(page, size);
            return Result.success(bookPage);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员获取书籍列表
     * @param title 书名（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 书籍列表
     */
    @GetMapping("/admin/list")
    public Result<IPage<BookPageVO>> getAdminBookList(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // TODO: 权限校验
            return Result.success(bookService.getAdminBookList(title, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error("获取书籍列表失败：" + e.getMessage());
        }
    }

    /**
     * 管理员删除书籍
     * @param id 书籍ID
     * @return 成功信息
     */
    @org.springframework.web.bind.annotation.DeleteMapping("/admin/{id}")
    public Result<String> adminDeleteBook(@PathVariable Long id) {
        try {
            // TODO: 权限校验
            bookService.adminDeleteBook(id);
            return Result.success("删除书籍成功");
        } catch (Exception e) {
            return Result.error("删除书籍失败：" + e.getMessage());
        }
    }

    /**
     * 管理员封禁书籍
     * @param id 书籍ID
     * @param reason 封禁原因
     * @return 成功信息
     */
    @PutMapping("/admin/ban/{id}")
    public Result<String> banBook(@PathVariable Long id, @RequestParam String reason) {
        try {
            // TODO: 权限校验
            bookService.banBook(id, reason);
            return Result.success("封禁书籍成功");
        } catch (Exception e) {
            return Result.error("封禁书籍失败：" + e.getMessage());
        }
    }

    /**
     * 管理员解封书籍
     * @param id 书籍ID
     * @return 成功信息
     */
    @PutMapping("/admin/unban/{id}")
    public Result<String> unbanBook(@PathVariable Long id) {
        try {
            // TODO: 权限校验
            bookService.unbanBook(id);
            return Result.success("解封书籍成功");
        } catch (Exception e) {
            return Result.error("解封书籍失败：" + e.getMessage());
        }
    }
}
