package com.booktrad.order.service;

import com.booktrad.order.dto.OrderCancelDTO;
import com.booktrad.order.dto.OrderCreateDTO;
import com.booktrad.order.vo.OrderVO;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单业务接口
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
public interface OrderService {

    /**
     * 创建订单
     * @param createDTO 创建订单DTO
     * @return 订单详情
     */
    OrderVO createOrder(OrderCreateDTO createDTO);

    /**
     * 卖家确认接单
     * @param orderId 订单ID
     * @param sellerPhone 卖家联系电话
     * @return 订单详情
     */
    OrderVO confirmOrder(Long orderId, String sellerPhone);

    /**
     * 买家确认收货
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO buyerConfirm(Long orderId);

    /**
     * 卖家确认交货
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO sellerConfirm(Long orderId);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @param cancelDTO 取消原因
     * @return 订单详情
     */
    OrderVO cancelOrder(Long orderId, OrderCancelDTO cancelDTO);

    /**
     * 查询订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId);

    /**
     * 查询用户的订单列表（买家或卖家）
     * @return 订单列表
     */
    List<OrderVO> getOrderList();

    /**
     * 查询买家的订单列表
     * @return 订单列表
     */
    List<OrderVO> getBuyerOrderList();

    /**
     * 查询卖家的订单列表
     * @return 订单列表
     */
    List<OrderVO> getSellerOrderList();
}
