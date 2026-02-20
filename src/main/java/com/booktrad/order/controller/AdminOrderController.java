package com.booktrad.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.common.result.Result;
import com.booktrad.order.service.OrderService;
import com.booktrad.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 管理员订单控制器
 * @Date 2026/2/20
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api/order/admin")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 管理员获取订单列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param orderNo 订单号（可选）
     * @return 订单分页列表
     */
    @GetMapping("/list")
    public Result<IPage<OrderVO>> getAdminOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo) {
        try {
            // TODO: 权限校验
            return Result.success(orderService.getAdminOrderList(pageNum, pageSize, orderNo));
        } catch (Exception e) {
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }
}
