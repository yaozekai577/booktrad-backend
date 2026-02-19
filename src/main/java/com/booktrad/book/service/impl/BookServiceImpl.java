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
        Page<Book> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Book> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        // 模糊查询
        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title);
        }
        
        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("created_at");
        
        IPage<Book> bookPage = bookMapper.selectPage(page, queryWrapper);
        
        // Convert to VO
        return bookPage.convert(book -> {
            BookPageVO vo = new BookPageVO();
            BeanUtils.copyProperties(book, vo);
            vo.setBookId(book.getId());
            // 处理封面图片（取第一张）
            if (book.getCoverImage() != null && !book.getCoverImage().isEmpty()) {
                vo.setCoverImg(book.getCoverImage().split(",")[0]);
            }
            
            // 设置描述信息
            BookVO tempVO = new BookVO();
            tempVO.setBookCondition(book.getBookCondition());
            tempVO.setStatus(book.getStatus());
            tempVO.setIsBanned(book.getIsBanned());
            setDescriptions(tempVO);
            
            // 将描述信息复制到BookPageVO中（如果BookPageVO有对应字段）
            // 注意：BookPageVO没有conditionDesc等字段，前端是直接显示status数字？
            // 检查AdminView.vue，发现使用了getStatusText方法处理status
            // 所以这里不需要额外处理
            
            return vo;
        });
    }

    @Override
    public void adminDeleteBook(Long bookId) {
        if (bookId == null) {
            throw new RuntimeException("书籍ID不能为空");
        }
        bookMapper.deleteById(bookId);
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
