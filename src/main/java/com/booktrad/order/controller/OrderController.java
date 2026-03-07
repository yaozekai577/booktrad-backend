package com.booktrad.order.controller;

import com.booktrad.common.result.Result;
import com.booktrad.order.dto.OrderCancelDTO;
import com.booktrad.order.dto.OrderCreateDTO;
import com.booktrad.order.dto.WantedSupplyOrderCreateDTO;
import com.booktrad.order.service.OrderService;
import com.booktrad.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单控制器
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderVO> createOrder(@RequestBody OrderCreateDTO createDTO) {
        OrderVO orderVO = orderService.createOrder(createDTO);
        return Result.success(orderVO);
    }

    @PostMapping("/wanted-supply")
    public Result<OrderVO> createWantedSupplyOrder(@RequestBody WantedSupplyOrderCreateDTO createDTO) {
        OrderVO orderVO = orderService.createWantedSupplyOrder(createDTO);
        return Result.success(orderVO);
    }

    /**
     * 卖家确认接单
     */
    @PutMapping("/{orderId}/confirm")
    public Result<OrderVO> confirmOrder(@PathVariable Long orderId, @RequestBody(required = false) java.util.Map<String, String> body) {
        String sellerPhone = body != null ? body.get("sellerPhone") : null;
        OrderVO orderVO = orderService.confirmOrder(orderId, sellerPhone);
        return Result.success(orderVO);
    }

    /**
     * 买家确认收货
     */
    @PutMapping("/{orderId}/buyer-confirm")
    public Result<OrderVO> buyerConfirm(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.buyerConfirm(orderId);
        return Result.success(orderVO);
    }

    /**
     * 卖家确认交货
     */
    @PutMapping("/{orderId}/seller-confirm")
    public Result<OrderVO> sellerConfirm(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.sellerConfirm(orderId);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<OrderVO> cancelOrder(@PathVariable Long orderId, @RequestBody OrderCancelDTO cancelDTO) {
        OrderVO orderVO = orderService.cancelOrder(orderId, cancelDTO);
        return Result.success(orderVO);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        OrderVO orderVO = orderService.getOrderDetail(orderId);
        return Result.success(orderVO);
    }

    /**
     * 查询用户的订单列表（买家或卖家）
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> getOrderList() {
        List<OrderVO> orderList = orderService.getOrderList();
        return Result.success(orderList);
    }

    /**
     * 查询买家的订单列表
     */
    @GetMapping("/buyer/list")
    public Result<List<OrderVO>> getBuyerOrderList() {
        List<OrderVO> orderList = orderService.getBuyerOrderList();
        return Result.success(orderList);
    }

    /**
     * 查询卖家的订单列表
     */
    @GetMapping("/seller/list")
    public Result<List<OrderVO>> getSellerOrderList() {
        List<OrderVO> orderList = orderService.getSellerOrderList();
        return Result.success(orderList);
    }
}
