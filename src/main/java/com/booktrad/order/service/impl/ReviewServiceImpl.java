package com.booktrad.order.service.impl;

import com.booktrad.common.context.UserContext;
import com.booktrad.order.dto.ReviewCreateDTO;
import com.booktrad.order.dto.ReviewReplyDTO;
import com.booktrad.order.entity.BookOrder;
import com.booktrad.order.entity.OrderReview;
import com.booktrad.order.mapper.OrderMapper;
import com.booktrad.order.mapper.ReviewMapper;
import com.booktrad.order.service.ReviewService;
import com.booktrad.order.vo.ReviewVO;
import com.booktrad.user.entity.User;
import com.booktrad.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 评价业务实现类
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewVO createReview(ReviewCreateDTO createDTO) {
        Long currentUserId = UserContext.getUserId();
        
        // 1. 查询订单信息
        BookOrder order = orderMapper.selectById(createDTO.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 验证订单状态（必须是已完成状态）
        if (order.getStatus() != 3) {
            throw new RuntimeException("只能对已完成的订单进行评价");
        }

        // 3. 判断当前用户是买家还是卖家
        Integer reviewerRole;
        Long revieweeId;
        
        if (currentUserId.equals(order.getBuyerId())) {
            // 当前用户是买家，评价卖家
            reviewerRole = 1;
            revieweeId = order.getSellerId();
            
            // 检查买家是否已评价
            if (order.getBuyerReviewed() != null && order.getBuyerReviewed() == 1) {
                throw new RuntimeException("您已经评价过该订单");
            }
        } else if (currentUserId.equals(order.getSellerId())) {
            // 当前用户是卖家，评价买家
            reviewerRole = 2;
            revieweeId = order.getBuyerId();
            
            // 检查卖家是否已评价
            if (order.getSellerReviewed() != null && order.getSellerReviewed() == 1) {
                throw new RuntimeException("您已经评价过该订单");
            }
        } else {
            throw new RuntimeException("您不是该订单的参与者，无法评价");
        }

        // 4. 验证评分范围
        if (createDTO.getRating() < 1 || createDTO.getRating() > 5) {
            throw new RuntimeException("评分必须在1-5星之间");
        }

        // 5. 创建评价记录
        OrderReview review = new OrderReview();
        review.setOrderId(createDTO.getOrderId());
        review.setReviewerId(currentUserId);
        review.setRevieweeId(revieweeId);
        review.setReviewerRole(reviewerRole);
        review.setRating(createDTO.getRating());
        review.setContent(createDTO.getContent());
        review.setIsAnonymous(createDTO.getIsAnonymous() != null ? createDTO.getIsAnonymous() : 0);
        
        reviewMapper.insert(review);

        // 6. 更新订单的评价状态
        if (reviewerRole == 1) {
            // 买家评价
            orderMapper.updateBuyerReviewed(createDTO.getOrderId());
        } else {
            // 卖家评价
            orderMapper.updateSellerReviewed(createDTO.getOrderId());
        }

        // 7. 更新被评价人的评分统计
        updateUserRating(revieweeId, reviewerRole, createDTO.getRating());

        // 8. 查询并返回评价详情
        return reviewMapper.selectReviewById(review.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewVO replyReview(Long reviewId, ReviewReplyDTO replyDTO) {
        Long currentUserId = UserContext.getUserId();
        
        // 1. 查询评价信息
        OrderReview review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        // 2. 验证当前用户是否是被评价人
        if (!currentUserId.equals(review.getRevieweeId())) {
            throw new RuntimeException("只有被评价人才能回复评价");
        }

        // 3. 检查是否已经回复过
        if (review.getReplyContent() != null && !review.getReplyContent().isEmpty()) {
            throw new RuntimeException("您已经回复过该评价");
        }

        // 4. 更新回复内容
        reviewMapper.updateReplyContent(reviewId, replyDTO.getReplyContent());

        // 5. 查询并返回评价详情
        return reviewMapper.selectReviewById(reviewId);
    }

    @Override
    public ReviewVO getReviewDetail(Long reviewId) {
        ReviewVO reviewVO = reviewMapper.selectReviewById(reviewId);
        if (reviewVO == null) {
            throw new RuntimeException("评价不存在");
        }
        return reviewVO;
    }

    @Override
    public List<ReviewVO> getReviewsByOrderId(Long orderId) {
        return reviewMapper.selectReviewsByOrderId(orderId);
    }

    @Override
    public List<ReviewVO> getReceivedReviews(Long userId) {
        return reviewMapper.selectReceivedReviews(userId);
    }

    @Override
    public List<ReviewVO> getGivenReviews(Long userId) {
        return reviewMapper.selectGivenReviews(userId);
    }

    @Override
    public List<ReviewVO> getMyReceivedReviews() {
        Long currentUserId = UserContext.getUserId();
        return reviewMapper.selectReceivedReviews(currentUserId);
    }

    @Override
    public List<ReviewVO> getMyGivenReviews() {
        Long currentUserId = UserContext.getUserId();
        return reviewMapper.selectGivenReviews(currentUserId);
    }

    /**
     * 更新用户评分统计
     * @param userId 用户ID
     * @param reviewerRole 评价人角色（1买家评价卖家 2卖家评价买家）
     * @param rating 评分
     */
    private void updateUserRating(Long userId, Integer reviewerRole, Integer rating) {
        // 使用手写SQL查询用户
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return;
        }

        if (reviewerRole == 1) {
            // 买家评价卖家，更新卖家评分
            Integer currentCount = user.getSellerRatingCount() != null ? user.getSellerRatingCount() : 0;
            Double currentScore = user.getSellerRatingScore() != null ? user.getSellerRatingScore() : 5.0;
            
            // 计算新的评分：(原总分 + 新评分) / (原评价数 + 1)
            double totalScore = currentScore * currentCount + rating;
            int newCount = currentCount + 1;
            double newScore = totalScore / newCount;
            
            // 保留两位小数
            BigDecimal bd = new BigDecimal(newScore);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            
            // 调用手写SQL更新
            userMapper.updateSellerRating(userId, bd.doubleValue(), newCount);
            
        } else if (reviewerRole == 2) {
            // 卖家评价买家，更新买家评分
            Integer currentCount = user.getBuyerRatingCount() != null ? user.getBuyerRatingCount() : 0;
            Double currentScore = user.getBuyerRatingScore() != null ? user.getBuyerRatingScore() : 5.0;
            
            // 计算新的评分
            double totalScore = currentScore * currentCount + rating;
            int newCount = currentCount + 1;
            double newScore = totalScore / newCount;
            
            // 保留两位小数
            BigDecimal bd = new BigDecimal(newScore);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            
            // 调用手写SQL更新
            userMapper.updateBuyerRating(userId, bd.doubleValue(), newCount);
        }
    }
}
