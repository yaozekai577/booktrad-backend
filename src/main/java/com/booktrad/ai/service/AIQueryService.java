package com.booktrad.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.vo.BookPageVO;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI查询服务接口 - 使用自然语言查询书籍
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface AIQueryService {
    
    /**
     * 使用自然语言查询书籍
     * @param query 用户的自然语言查询
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    IPage<BookPageVO> queryBooksByNaturalLanguage(String query, Integer pageNum, Integer pageSize);
}
