package com.booktrad.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrad.book.entity.Book;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.common.context.UserContext;
import com.booktrad.order.dto.OrderCancelDTO;
import com.booktrad.order.dto.OrderCreateDTO;
import com.booktrad.order.entity.BookOrder;
import com.booktrad.order.mapper.OrderMapper;
import com.booktrad.order.service.OrderService;
import com.booktrad.order.vo.OrderVO;
import com.booktrad.user.entity.User;
import com.booktrad.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单业务实现类
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO createDTO) {
        Long currentUserId = UserContext.getUserId();

        // 查询书籍信息
        Book book = bookMapper.selectById(createDTO.getBookId());
        if (book == null) {
            throw new RuntimeException("书籍不存在");
        }

        // 查询买家信息
        User buyer = userMapper.selectById(currentUserId);
        if (buyer == null) {
            throw new RuntimeException("用户不存在");
        }

        // 查询卖家信息
        User seller = userMapper.selectById(createDTO.getSellerId());
        if (seller == null) {
            throw new RuntimeException("卖家不存在");
        }

        // 创建订单
        BookOrder order = new BookOrder();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(currentUserId);
        order.setSellerId(createDTO.getSellerId());
        order.setBookId(createDTO.getBookId());
        order.setBookTitle(book.getTitle());
        order.setBookCover(book.getCoverImage());
        order.setOriginalPrice(book.getPrice());
        order.setPrice(createDTO.getPrice());
        order.setTradeType(1); // 默认线下面交
        order.setMeetLocation(createDTO.getMeetLocation());
        order.setMeetTime(createDTO.getMeetTime());
        order.setBuyerPhone(createDTO.getBuyerPhone());
        order.setSellerPhone(seller.getPhone());
        order.setRemark(createDTO.getRemark());
        order.setStatus(1); // 待确认
        order.setBuyerConfirmed(0);
        order.setSellerConfirmed(0);

        orderMapper.insert(order);

        return getOrderDetail(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO confirmOrder(Long orderId) {
        Long currentUserId = UserContext.getUserId();

        BookOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证是否是卖家
        if (!order.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("只有卖家可以确认接单");
        }

        // 验证订单状态
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }

        // 更新订单状态
        order.setStatus(2); // 已确认、待交易
        order.setConfirmedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        return getOrderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO buyerConfirm(Long orderId) {
        Long currentUserId = UserContext.getUserId();

        BookOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证是否是买家
        if (!order.getBuyerId().equals(currentUserId)) {
            throw new RuntimeException("只有买家可以确认收货");
        }

        // 验证订单状态
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        // 买家确认收货
        order.setBuyerConfirmed(1);
        order.setBuyerConfirmedAt(LocalDateTime.now());

        // 检查是否双方都已确认
        if (order.getSellerConfirmed() == 1) {
            // 双方都确认，订单完成
            order.setStatus(3);
            order.setCompletedAt(LocalDateTime.now());
            // TODO: 更新书籍状态为已售出
        }

        orderMapper.updateById(order);

        return getOrderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO sellerConfirm(Long orderId) {
        Long currentUserId = UserContext.getUserId();

        BookOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证是否是卖家
        if (!order.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("只有卖家可以确认交货");
        }

        // 验证订单状态
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        // 卖家确认交货
        order.setSellerConfirmed(1);
        order.setSellerConfirmedAt(LocalDateTime.now());

        // 检查是否双方都已确认
        if (order.getBuyerConfirmed() == 1) {
            // 双方都确认，订单完成
            order.setStatus(3);
            order.setCompletedAt(LocalDateTime.now());
            // TODO: 更新书籍状态为已售出
        }

        orderMapper.updateById(order);

        return getOrderDetail(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO cancelOrder(Long orderId, OrderCancelDTO cancelDTO) {
        Long currentUserId = UserContext.getUserId();

        BookOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证是否是买家或卖家
        if (!order.getBuyerId().equals(currentUserId) && !order.getSellerId().equals(currentUserId)) {
            throw new RuntimeException("无权取消此订单");
        }

        // 验证订单状态（只有待确认和已确认状态可以取消）
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new RuntimeException("当前订单状态不允许取消");
        }

        // 如果已确认且有一方已确认收货/交货，不允许取消
        if (order.getStatus() == 2 && (order.getBuyerConfirmed() == 1 || order.getSellerConfirmed() == 1)) {
            throw new RuntimeException("订单已开始确认流程，不允许取消");
        }

        // 取消订单
        order.setStatus(4);
        order.setCancelReason(cancelDTO.getCancelReason());
        order.setCancelledAt(LocalDateTime.now());
        orderMapper.updateById(order);

        return getOrderDetail(orderId);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        OrderVO orderVO = orderMapper.selectOrderDetailById(orderId);
        if (orderVO == null) {
            throw new RuntimeException("订单不存在");
        }

        // 设置状态文本
        orderVO.setStatusText(getStatusText(orderVO));

        return orderVO;
    }

    @Override
    public List<OrderVO> getOrderList() {
        Long currentUserId = UserContext.getUserId();
        List<OrderVO> orderList = orderMapper.selectOrderListByUserId(currentUserId);

        // 设置状态文本
        for (OrderVO orderVO : orderList) {
            orderVO.setStatusText(getStatusText(orderVO));
        }

        return orderList;
    }

    @Override
    public List<OrderVO> getBuyerOrderList() {
        Long currentUserId = UserContext.getUserId();
        List<OrderVO> orderList = orderMapper.selectBuyerOrderList(currentUserId);

        // 设置状态文本
        for (OrderVO orderVO : orderList) {
            orderVO.setStatusText(getStatusText(orderVO));
        }

        return orderList;
    }

    @Override
    public List<OrderVO> getSellerOrderList() {
        Long currentUserId = UserContext.getUserId();
        List<OrderVO> orderList = orderMapper.selectSellerOrderList(currentUserId);

        // 设置状态文本
        for (OrderVO orderVO : orderList) {
            orderVO.setStatusText(getStatusText(orderVO));
        }

        return orderList;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 格式：yyyyMMddHHmmss + 6位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return timestamp + random;
    }

    /**
     * 获取订单状态文本
     */
    private String getStatusText(OrderVO order) {
        if (order.getStatus() == 1) {
            return "待确认";
        } else if (order.getStatus() == 2) {
            // 已确认、待交易状态，需要根据确认情况细分
            if (order.getBuyerConfirmed() == 0 && order.getSellerConfirmed() == 0) {
                return "等待面交";
            } else if (order.getBuyerConfirmed() == 0 && order.getSellerConfirmed() == 1) {
                return "等待买家确认收货";
            } else if (order.getBuyerConfirmed() == 1 && order.getSellerConfirmed() == 0) {
                return "等待卖家确认交货";
            }
        } else if (order.getStatus() == 3) {
            return "已完成";
        } else if (order.getStatus() == 4) {
            return "已取消";
        }
        return "未知状态";
    }
}
