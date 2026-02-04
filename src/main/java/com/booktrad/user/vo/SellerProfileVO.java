package com.booktrad.user.vo;

import com.booktrad.book.vo.BookListVO;
import com.booktrad.order.vo.ReviewVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 
 * 项目名称：booktrad 
 * 版本：V1.0 
 * 
 * @Author yaozekai 
 * @Email 2321593248@qq.com 
 * @Description  卖家主页视图对象，包含卖家基本信息、评价列表和在售书籍列表
 * @Date 2026-02-04 
 * Copyrigt (C) 2025-2026 All Right Reserved 
 * 注意：本内容为个人毕设 
 */
@Data
public class SellerProfileVO {
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 卖家用户名
     */
    private String username;
    
    /**
     * 卖家邮箱
     */
    private String email;
    
    /**
     * 卖家手机号
     */
    private String phone;
    
    /**
     * 卖家角色（0-普通用户，1-管理员）
     */
    private Integer role;
    
    /**
     * 卖家状态（0-正常，1-禁用）
     */
    private Integer status;
    
    /**
     * 卖家作为卖家的评分
     */
    private Double sellerRatingScore;
    
    /**
     * 卖家作为卖家的评价次数
     */
    private Integer sellerRatingCount;
    
    /**
     * 卖家注册时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 卖家额外信息
     */
    private String extraInfo;
    
    /**
     * 卖家收到的评价列表
     */
    private List<ReviewVO> reviews;
    
    /**
     * 卖家当前在售的书籍列表
     */
    private List<BookListVO> books;
}
