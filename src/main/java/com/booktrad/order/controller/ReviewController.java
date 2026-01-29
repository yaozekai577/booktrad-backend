package com.booktrad.order.controller;

import com.booktrad.common.result.Result;
import com.booktrad.order.dto.ReviewCreateDTO;
import com.booktrad.order.dto.ReviewReplyDTO;
import com.booktrad.order.service.ReviewService;
import com.booktrad.order.vo.ReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 评价控制器
 * @Date 2026/1/29
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 创建评价
     * @param createDTO 创建评价DTO
     * @return 评价详情
     */
    @PostMapping
    public Result<ReviewVO> createReview(@RequestBody ReviewCreateDTO createDTO) {
        log.info("创建评价，订单ID：{}", createDTO.getOrderId());
        try {
            ReviewVO reviewVO = reviewService.createReview(createDTO);
            return Result.success("评价成功", reviewVO);
        } catch (Exception e) {
            log.error("创建评价失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 回复评价
     * @param reviewId 评价ID
     * @param replyDTO 回复内容DTO
     * @return 评价详情
     */
    @PostMapping("/{reviewId}/reply")
    public Result<ReviewVO> replyReview(@PathVariable Long reviewId, 
                                        @RequestBody ReviewReplyDTO replyDTO) {
        log.info("回复评价，评价ID：{}", reviewId);
        try {
            ReviewVO reviewVO = reviewService.replyReview(reviewId, replyDTO);
            return Result.success("回复成功", reviewVO);
        } catch (Exception e) {
            log.error("回复评价失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询评价详情
     * @param reviewId 评价ID
     * @return 评价详情
     */
    @GetMapping("/{reviewId}")
    public Result<ReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        log.info("查询评价详情，评价ID：{}", reviewId);
        try {
            ReviewVO reviewVO = reviewService.getReviewDetail(reviewId);
            return Result.success(reviewVO);
        } catch (Exception e) {
            log.error("查询评价详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据订单ID查询评价列表
     * @param orderId 订单ID
     * @return 评价列表
     */
    @GetMapping("/order/{orderId}")
    public Result<List<ReviewVO>> getReviewsByOrderId(@PathVariable Long orderId) {
        log.info("查询订单评价列表，订单ID：{}", orderId);
        try {
            List<ReviewVO> reviews = reviewService.getReviewsByOrderId(orderId);
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("查询订单评价列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询指定用户收到的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    @GetMapping("/user/{userId}/received")
    public Result<List<ReviewVO>> getReceivedReviews(@PathVariable Long userId) {
        log.info("查询用户收到的评价列表，用户ID：{}", userId);
        try {
            List<ReviewVO> reviews = reviewService.getReceivedReviews(userId);
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("查询用户收到的评价列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询指定用户发出的评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    @GetMapping("/user/{userId}/given")
    public Result<List<ReviewVO>> getGivenReviews(@PathVariable Long userId) {
        log.info("查询用户发出的评价列表，用户ID：{}", userId);
        try {
            List<ReviewVO> reviews = reviewService.getGivenReviews(userId);
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("查询用户发出的评价列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询当前用户收到的评价列表
     * @return 评价列表
     */
    @GetMapping("/my/received")
    public Result<List<ReviewVO>> getMyReceivedReviews() {
        log.info("查询当前用户收到的评价列表");
        try {
            List<ReviewVO> reviews = reviewService.getMyReceivedReviews();
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("查询当前用户收到的评价列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询当前用户发出的评价列表
     * @return 评价列表
     */
    @GetMapping("/my/given")
    public Result<List<ReviewVO>> getMyGivenReviews() {
        log.info("查询当前用户发出的评价列表");
        try {
            List<ReviewVO> reviews = reviewService.getMyGivenReviews();
            return Result.success(reviews);
        } catch (Exception e) {
            log.error("查询当前用户发出的评价列表失败", e);
            return Result.error(e.getMessage());
        }
    }
}
