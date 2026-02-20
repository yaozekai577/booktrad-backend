package com.booktrad.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.common.result.Result;
import com.booktrad.order.service.ReviewService;
import com.booktrad.order.vo.ReviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 管理员评价控制器
 * @Date 2026/2/20
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api/review/admin")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 管理员获取评价列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评价分页列表
     */
    @GetMapping("/list")
    public Result<IPage<ReviewVO>> getAdminReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // TODO: 权限校验
            return Result.success(reviewService.getAdminReviewList(pageNum, pageSize));
        } catch (Exception e) {
            return Result.error("获取评价列表失败：" + e.getMessage());
        }
    }

    /**
     * 管理员删除评价
     * @param reviewId 评价ID
     * @return 成功信息
     */
    @DeleteMapping("/{reviewId}")
    public Result<String> deleteReview(@PathVariable Long reviewId) {
        try {
            // TODO: 权限校验
            reviewService.deleteReview(reviewId);
            return Result.success("删除评价成功");
        } catch (Exception e) {
            return Result.error("删除评价失败：" + e.getMessage());
        }
    }
}
