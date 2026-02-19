package com.booktrad.book.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.booktrad.book.dto.BookPublishDTO;
import com.booktrad.book.dto.BookQueryDTO;
import com.booktrad.book.dto.BookUpdateDTO;
import com.booktrad.book.entity.Book;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.book.service.BookService;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookPublishVO;
import com.booktrad.book.vo.BookVO;
import com.booktrad.common.context.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍服务实现类
 * @Date 2025/12/29 21:22
 * Copyright (C) 2025-2026 All Rights Reserved.
 * 注意：本内容为个人毕设
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public BookVO getBookDetail(Long id) {
        // 1. 查询书籍详情
        BookVO bookVO = bookMapper.selectBookDetailById(id);

        // 2. 判断书籍是否存在
        if (bookVO == null) {
            throw new RuntimeException("书籍不存在或已被删除");
        }
        
        // 3. 增加浏览次数
        bookMapper.incrementViewCount(id);
        // 更新VO中的浏览次数，避免前端显示旧数据
        if (bookVO.getViewCount() != null) {
            bookVO.setViewCount(bookVO.getViewCount() + 1);
        } else {
            bookVO.setViewCount(1);
        }

        // 4. 查询卖家的在售和已售数量
        Long sellerId = bookVO.getSellerId();
        if (sellerId != null) {
            Integer onSaleCount = bookMapper.countSellerOnSaleBooks(sellerId);
            Integer soldCount = bookMapper.countSellerSoldBooks(sellerId);
            bookVO.setSellerOnSaleCount(onSaleCount != null ? onSaleCount : 0);
            bookVO.setSellerSoldCount(soldCount != null ? soldCount : 0);
        }

        // 5. 处理封面图片，将String转为List<String>
        this.handleCoverImage(bookVO);

        // 6. 设置描述信息
        this.setDescriptions(bookVO);

        return bookVO;
    }

    @Override
    public IPage<BookPageVO> getBookPage(BookQueryDTO bookQueryDTO) {
        // 1. 参数验证：价格区间
        if (bookQueryDTO.getMinPrice() != null && bookQueryDTO.getMaxPrice() != null) {
            if (bookQueryDTO.getMinPrice().compareTo(bookQueryDTO.getMaxPrice()) > 0) {
                throw new RuntimeException("最小价格不能大于最大价格");
            }
        }

        // 2. 设置默认值
        int page = bookQueryDTO.getPage() != null && bookQueryDTO.getPage() > 0 ? bookQueryDTO.getPage() : 1;
        int size = bookQueryDTO.getSize() != null && bookQueryDTO.getSize() > 0 ? bookQueryDTO.getSize() : 6;
        bookQueryDTO.setPage(page);
        bookQueryDTO.setSize(size);

        // 3. 创建分页对象
        Page<BookPageVO> pageParam = new Page<>(page, size);

        // 4. 调用Mapper查询分页数据
        IPage<BookPageVO> bookPage = bookMapper.selectBookPage(pageParam, bookQueryDTO);

        return bookPage;
    }

    @Override
    public BookPublishVO publishBook(BookPublishDTO bookPublishDTO, Long sellerId) {
        // 1. 参数校验
        if (bookPublishDTO.getTitle() == null || bookPublishDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("书名不能为空");
        }
        if (bookPublishDTO.getPrice() == null || bookPublishDTO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("出售价格必须大于0");
        }

        // 2. 将DTO转换为Book实体
        Book book = new Book();
        BeanUtils.copyProperties(bookPublishDTO, book);

        // 3. 设置卖家ID（从当前登录用户中获取）
        book.setSellerId(sellerId);

        // 4. 设置默认值
        book.setStatus(1); // 状态：1-在售
        book.setIsBanned(0); // 是否封禁：0-正常
        book.setViewCount(0); // 浏览次数：0

        // 5. 使用MyBatis-Plus的insert方法插入数据库
        int result = bookMapper.insert(book);

        // 6. 判断插入是否成功
        if (result <= 0) {
            throw new RuntimeException("书籍发布失败");
        }

        // 7. 返回发布结果
        BookPublishVO bookPublishVO = new BookPublishVO();
        bookPublishVO.setBookId(book.getId());
        return bookPublishVO;
    }

    @Override
    public BookVO updateBook(BookUpdateDTO bookUpdateDTO) {
        // 1. 参数校验
        if (bookUpdateDTO.getId() == null) {
            throw new RuntimeException("书籍ID不能为空");
        }
        if (bookUpdateDTO.getTitle() == null || bookUpdateDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("书名不能为空");
        }
        if (bookUpdateDTO.getPrice() == null || bookUpdateDTO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("出售价格必须大于0");
        }

        // 2. 查询原书籍信息
        Book existingBook = bookMapper.selectBookForOrder(bookUpdateDTO.getId());
        if (existingBook == null) {
            throw new RuntimeException("书籍不存在");
        }

        // 3. 权限校验：只有卖家本人可以修改
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        if (!existingBook.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("无权修改此书籍");
        }

        // 4. 状态校验：只有在售和已下架的书籍可以修改
        if (existingBook.getStatus() != 1 && existingBook.getStatus() != 2) {
            throw new RuntimeException("当前状态的书籍不允许修改");
        }

        // 5. 将DTO数据复制到实体
        BeanUtils.copyProperties(bookUpdateDTO, existingBook);

        // 6. 更新数据库（使用手写SQL）
        int result = bookMapper.updateBookInfo(existingBook);
        if (result <= 0) {
            throw new RuntimeException("书籍更新失败");
        }

        // 7. 返回更新后的书籍详情
        return getBookDetail(bookUpdateDTO.getId());
    }

    @Override
    public BookVO offShelfBook(Long bookId) {
        // 1. 参数校验
        if (bookId == null) {
            throw new RuntimeException("书籍ID不能为空");
        }

        // 2. 查询书籍信息
        Book book = bookMapper.selectBookForOrder(bookId);
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }

        // 3. 权限校验：只有卖家本人可以下架
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        if (!book.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("无权下架此书籍");
        }

        // 4. 状态校验：只有在售的书籍可以下架
        if (book.getStatus() != 1) {
            throw new RuntimeException("只有在售状态的书籍可以下架");
        }

        // 5. 更新书籍状态为已下架
        int result = bookMapper.updateBookStatus(bookId, 2);
        if (result <= 0) {
            throw new RuntimeException("下架失败");
        }

        // 6. 返回下架后的书籍详情
        return getBookDetail(bookId);
    }

    @Override
    public IPage<BookPageVO> getSellerBookPage(Integer page, Integer size) {
        // ... (existing code)
        // 1. 获取当前登录用户ID
        Long sellerId = UserContext.getUserId();
        if (sellerId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 设置默认值
        int currentPage = page != null && page > 0 ? page : 1;
        int pageSize = size != null && size > 0 ? size : 10;

        // 3. 创建分页对象
        Page<BookPageVO> pageParam = new Page<>(currentPage, pageSize);

        // 4. 调用Mapper查询分页数据
        // IPage<BookPageVO> bookPage = bookMapper.selectSellerBookPage(pageParam, sellerId); // This was in original code
        return bookMapper.selectSellerBookPage(pageParam, sellerId);
    }

    @Override
    public IPage<BookPageVO> getAdminBookList(String title, Integer pageNum, Integer pageSize) {
        Page<BookPageVO> page = new Page<>(pageNum, pageSize);
        BookQueryDTO queryDTO = new BookQueryDTO();
        queryDTO.setKeyword(title); // 复用 keyword 字段进行模糊查询
        // 管理员查询不需要过滤状态，所以这里不需要设置 status 等条件
        
        // 调用 Mapper 的 selectBookPage 方法，或者是专门为管理员写的查询方法
        // 由于 selectBookPage 里面有 WHERE b.status = 1 AND b.is_banned = 0 的限制，不能直接复用
        // 我们需要新增一个 selectAdminBookPage 方法，或者使用 QueryWrapper 查询 entity 然后转换
        
        // 使用 QueryWrapper 方案（目前采用的）：
        // Page<Book> bookPage = new Page<>(pageNum, pageSize);
        // ...
        // IPage<Book> resultPage = bookMapper.selectPage(bookPage, queryWrapper);
        // return resultPage.convert(...)
        
        // 但是 Book 实体类没有 sellerName 字段，需要关联查询
        // 所以最好是在 XML 中新增一个 selectAdminBookPage
        
        return bookMapper.selectAdminBookPage(page, title);
    }

    @Override
    public void adminDeleteBook(Long bookId) {
        if (bookId == null) {
            throw new RuntimeException("书籍ID不能为空");
        }
        bookMapper.deleteById(bookId);
    }

    @Override
    public void banBook(Long bookId, String reason) {
        if (bookId == null) {
            throw new RuntimeException("书籍ID不能为空");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("封禁原因不能为空");
        }

        Book book = bookMapper.selectBookEntityById(bookId);
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }

        bookMapper.updateBookBanStatus(bookId, 1, reason);
    }

    @Override
    public void unbanBook(Long bookId) {
        if (bookId == null) {
            throw new RuntimeException("书籍ID不能为空");
        }

        Book book = bookMapper.selectBookEntityById(bookId);
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }

        bookMapper.updateBookBanStatus(bookId, 0, null);
    }

    /**
     * 处理封面图片，将String转为List<String>
     * 假设封面图片以逗号分隔，例如："url1,url2,url3"
     * @param bookVO 书籍详情VO
     */
    private void handleCoverImage(BookVO bookVO) {
        String coverImageStr = bookVO.getCoverImageStr() != null ? bookVO.getCoverImageStr() : "";
        List<String> coverImageList = new ArrayList<>();
        
        if (!coverImageStr.isEmpty()) {
            String[] urls = coverImageStr.split(",");
            for (String url : urls) {
                if (!url.trim().isEmpty()) {
                    coverImageList.add(url.trim());
                }
            }
        }
        
        bookVO.setCoverImage(coverImageList);
    }

    /**
     * 设置描述信息：成色描述、状态描述、是否封禁描述
     * @param bookVO 书籍详情VO
     */
    private void setDescriptions(BookVO bookVO) {
        // 设置成色描述
        switch (bookVO.getBookCondition()) {
            case 1: bookVO.setBookConditionDesc("全新"); break;
            case 2: bookVO.setBookConditionDesc("九成新"); break;
            case 3: bookVO.setBookConditionDesc("八成新"); break;
            case 4: bookVO.setBookConditionDesc("明显使用痕迹"); break;
            default: bookVO.setBookConditionDesc("未知");
        }

        // 设置状态描述
        switch (bookVO.getStatus()) {
            case 1: bookVO.setStatusDesc("在售"); break;
            case 2: bookVO.setStatusDesc("已下架"); break;
            case 3: bookVO.setStatusDesc("已售出"); break;
            default: bookVO.setStatusDesc("未知");
        }

        // 设置是否封禁描述
        if (bookVO.getIsBanned() != null) {
            bookVO.setIsBannedDesc(bookVO.getIsBanned() == 0 ? "正常" : "已封禁");
        } else {
            bookVO.setIsBannedDesc("正常");
        }
    }
}
