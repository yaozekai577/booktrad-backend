package com.booktrad.book.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.book.service.BookService;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookVO;
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

        // 3. 处理封面图片，将String转为List<String>
        this.handleCoverImage(bookVO);

        // 4. 设置描述信息
        this.setDescriptions(bookVO);

        return bookVO;
    }

    @Override
    public IPage<BookPageVO> getBookPage(Integer current, Integer size, String keyword, Long categoryId) {
        // 1. 设置默认值
        int page = current != null && current > 0 ? current : 1;
        int pageSize = size != null && size > 0 ? size : 6;

        // 2. 创建分页对象
        Page<BookPageVO> pageParam = new Page<>(page, pageSize);

        // 3. 调用Mapper查询分页数据
        IPage<BookPageVO> bookPage = bookMapper.selectBookPage(pageParam, keyword, categoryId);

        return bookPage;
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
