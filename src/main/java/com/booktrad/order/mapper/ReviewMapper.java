package com.booktrad.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.order.entity.OrderReview;
import com.booktrad.order.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 评价Mapper接口
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface ReviewMapper extends BaseMapper<OrderReview> {

    /**
     * 根据评价ID查询评价详情（包含用户名）
     * @param reviewId 评价ID
     * @return 评价详情
     */
    ReviewVO selectReviewById(@Param("reviewId") Long reviewId);

    /**
     * 根据订单ID查询评价列表（包含用户名）
     * @param orderId 订单ID
     * @return 评价列表
     */
    List<ReviewVO> selectReviewsByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询用户收到的评价列表（作为被评价人）
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewVO> selectReceivedReviews(@Param("userId") Long userId);

    /**
     * 查询用户发出的评价列表（作为评价人）
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewVO> selectGivenReviews(@Param("userId") Long userId);
}
