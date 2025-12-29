package com.booktrad.book.service;

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
}
