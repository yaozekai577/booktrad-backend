package com.booktrad.book.mapper;

import com.booktrad.book.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 书籍Mapper接口
 * @Date 2025/12/29 21:30
 * Copyrigt (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface BookMapper {

    /**
     * 根据ID查询书籍详情
     * @param id 书籍ID
     * @return 书籍详情VO
     */
    BookVO selectBookDetailById(Long id);
}