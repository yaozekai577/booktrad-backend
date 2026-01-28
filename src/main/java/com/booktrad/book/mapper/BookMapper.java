package com.booktrad.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.book.dto.BookQueryDTO;
import com.booktrad.book.entity.Book;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.book.vo.BookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 根据ID查询书籍详情
     * @param id 书籍ID
     * @return 书籍详情VO
     */
    BookVO selectBookDetailById(Long id);

    /**
     * 首页书籍分页查询
     * 只查询"在售、未封禁、未删除"的书籍
     * @param page 分页对象
     * @param bookQueryDTO 查询条件DTO
     * @return 分页结果
     */
    IPage<BookPageVO> selectBookPage(IPage<BookPageVO> page, @Param("query") BookQueryDTO bookQueryDTO);
    /**
     * 增加书籍浏览次数
     * @param id 书籍ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 根据ID查询书籍（用于订单创建，忽略逻辑删除）
     * @param id 书籍ID
     * @return 书籍实体
     */
    Book selectBookForOrder(@Param("id") Long id);

    /**
     * 更新书籍状态为已售出
     * @param bookId 书籍ID
     * @param soldAt 成交时间
     * @return 影响行数
     */
    int updateBookStatusToSold(@Param("bookId") Long bookId, @Param("soldAt") java.time.LocalDateTime soldAt);

    /**
     * 更新书籍状态
     * @param bookId 书籍ID
     * @param status 状态：1-在售 2-已下架 3-交易中 4-已售出
     * @return 影响行数
     */
    int updateBookStatus(@Param("bookId") Long bookId, @Param("status") Integer status);

    /**
     * 更新书籍信息
     * @param book 书籍实体
     * @return 影响行数
     */
    int updateBookInfo(Book book);

    /**
     * 分页查询卖家的所有书籍（包括已下架、已封禁的）
     * @param page 分页对象
     * @param sellerId 卖家ID
     * @return 分页结果
     */
    IPage<BookPageVO> selectSellerBookPage(IPage<BookPageVO> page, @Param("sellerId") Long sellerId);

    /**
     * 统计卖家在售书籍数量
     * @param sellerId 卖家ID
     * @return 在售数量
     */
    Integer countSellerOnSaleBooks(@Param("sellerId") Long sellerId);

    /**
     * 统计卖家已售书籍数量
     * @param sellerId 卖家ID
     * @return 已售数量
     */
    Integer countSellerSoldBooks(@Param("sellerId") Long sellerId);
}