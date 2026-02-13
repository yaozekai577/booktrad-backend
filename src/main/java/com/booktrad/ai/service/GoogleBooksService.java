package com.booktrad.ai.service;

import com.booktrad.ai.dto.BookInfoDTO;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description Google Books API服务接口
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface GoogleBooksService {
    
    /**
     * 根据ISBN查询书籍信息
     * @param isbn ISBN号
     * @return 书籍信息DTO
     */
    BookInfoDTO getBookByISBN(String isbn);
    
    /**
     * 根据书名查询书籍信息
     * @param title 书名
     * @return 书籍信息DTO
     */
    BookInfoDTO getBookByTitle(String title);
}
