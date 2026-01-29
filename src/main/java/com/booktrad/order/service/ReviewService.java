package com.booktrad.order.service;

import com.booktrad.order.dto.ReviewCreateDTO;
import com.booktrad.order.dto.ReviewReplyDTO;
import com.booktrad.order.vo.ReviewVO;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 评价业务接口
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface ReviewService {

    /**
     * 创建评价
     * @param createDTO 创建评价DTO
     * @return 评价详情
     */
    ReviewVO createReview(ReviewCreateDTO createDTO);

    /**
     * 回复评价
     * @param reviewId 评价ID
     * @param replyDTO 回复内容DTO
     * @return 评价详情
     */
    ReviewVO replyReview(Long reviewId, ReviewReplyDTO replyDTO);

    /**
     * 查询评价详情
     * @param reviewId 评价ID
     * @return 评价详情
     */
    ReviewVO getReviewDetail(Long reviewId);

    /**
     * 根据订单ID查询评价列表
     * @param orderId 订单ID
     * @return 评价列表
     */
    List<ReviewVO> getReviewsByOrderId(Long orderId);

    /**
     * 查询用户收到的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewVO> getReceivedReviews(Long userId);

    /**
     * 查询用户发出的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<ReviewVO> getGivenReviews(Long userId);

    /**
     * 查询当前用户收到的评价列表
     * @return 评价列表
     */
    List<ReviewVO> getMyReceivedReviews();

    /**
     * 查询当前用户发出的评价列表
     * @return 评价列表
     */
    List<ReviewVO> getMyGivenReviews();
}
