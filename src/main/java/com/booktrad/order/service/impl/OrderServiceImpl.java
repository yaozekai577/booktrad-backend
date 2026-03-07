package com.booktrad.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.booktrad.book.entity.Book;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.common.context.UserContext;
import com.booktrad.order.dto.OrderCancelDTO;
import com.booktrad.order.dto.OrderCreateDTO;
import com.booktrad.order.dto.WantedSupplyOrderCreateDTO;
import com.booktrad.order.entity.BookOrder;
import com.booktrad.order.mapper.OrderMapper;
import com.booktrad.order.service.OrderService;
import com.booktrad.order.vo.OrderVO;
import com.booktrad.user.entity.User;
import com.booktrad.user.mapper.UserMapper;
import com.booktrad.wanted.entity.WantedRequest;
import com.booktrad.wanted.mapper.WantedMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final WantedMapper wantedMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO createDTO) {
        try {
            Long currentUserId = UserContext.getUserId();
            if (currentUserId == null) {
                throw new RuntimeException("用户未登录");
            }

            // 查询书籍信息（使用手写SQL）
            Book book = bookMapper.selectBookForOrder(createDTO.getBookId());
            if (book == null) {
                throw new RuntimeException("书籍不存在");
            }

            // 验证卖家ID是否匹配
            if (!book.getSellerId().equals(createDTO.getSellerId())) {
                throw new RuntimeException("卖家信息不匹配");
            }

            // 验证书籍状态（只能对在售的书籍创建订单）
            if (book.getStatus() != 1) {
                throw new RuntimeException("书籍当前不可购买");
            }

            // 查询买家信息（使用手写SQL）
            User buyer = userMapper.selectUserForOrder(currentUserId);
            if (buyer == null) {
                throw new RuntimeException("买家用户不存在");
            }

            // 查询卖家信息（使用手写SQL）
            User seller = userMapper.selectUserForOrder(createDTO.getSellerId());
            if (seller == null) {
                throw new RuntimeException("卖家用户不存在");
            }

            // 创建订单
            BookOrder order = new BookOrder();
            order.setOrderNo(generateOrderNo());
            order.setBuyerId(currentUserId);
            order.setSellerId(createDTO.getSellerId());
            order.setBookId(createDTO.getBookId());
            order.setWantedId(0L);
            order.setBookTitle(book.getTitle());
            order.setBookCover(book.getCoverImage());
            order.setOriginalPrice(book.getPrice());
            order.setPrice(createDTO.getPrice());
            order.setTradeType(1); // 默认线下面交
            order.setMeetLocation(createDTO.getMeetLocation());
            order.setMeetTime(createDTO.getMeetTime());
            order.setBuyerPhone(createDTO.getBuyerPhone());
            order.setSellerPhone(seller.getPhone() != null ? seller.getPhone() : "");
            order.setRemark(createDTO.getRemark());
            order.setStatus(1); // 待确认
            order.setBuyerConfirmed(0);
            order.setSellerConfirmed(0);
            order.setBuyerReviewed(0);
            order.setSellerReviewed(0);

            orderMapper.insert(order);

            // 将书籍状态改为交易中
            bookMapper.updateBookStatus(order.getBookId(), 3);

            return getOrderDetail(order.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建订单失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createWantedSupplyOrder(WantedSupplyOrderCreateDTO createDTO) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new RuntimeException("用户未登录");
        }
        if (createDTO == null || createDTO.getWantedId() == null) {
            throw new RuntimeException("求购ID不能为空");
        }

        WantedRequest wanted = wantedMapper.selectWantedEntityById(createDTO.getWantedId());
        if (wanted == null) {
            throw new RuntimeException("求购信息不存在");
        }
        if (wanted.getStatus() == null || wanted.getStatus() != 1) {
            throw new RuntimeException("该求购已关闭");
        }
        if (currentUserId.equals(wanted.getBuyerId())) {
            throw new RuntimeException("不能对自己的求购供给");
        }

        BookOrder existed = orderMapper.selectOne(new LambdaQueryWrapper<BookOrder>()
                .eq(BookOrder::getBookId, 0L)
                .eq(BookOrder::getWantedId, wanted.getId())
                .eq(BookOrder::getBuyerId, wanted.getBuyerId())
                .eq(BookOrder::getSellerId, currentUserId)
                .in(BookOrder::getStatus, 1, 2));
        if (existed != null) {
            return getOrderDetail(existed.getId());
        }

        User seller = userMapper.selectUserForOrder(currentUserId);
        if (seller == null) {
            throw new RuntimeException("卖家用户不存在");
        }

        BookOrder order = new BookOrder();
        BigDecimal wantedPrice = wanted.getBudget() == null ? BigDecimal.ZERO : wanted.getBudget();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(wanted.getBuyerId());
        order.setSellerId(currentUserId);
        order.setBookId(0L);
        order.setWantedId(wanted.getId());
        order.setBookTitle(wanted.getTitle() == null || wanted.getTitle().trim().isEmpty() ? "求购供给订单" : wanted.getTitle());
        order.setBookCover(null);
        order.setOriginalPrice(wantedPrice);
        order.setPrice(wantedPrice);
        order.setTradeType(1);
        order.setMeetLocation(wanted.getExpectedLocation());
        order.setBuyerPhone(wanted.getContactPhone());
        order.setSellerPhone(seller.getPhone() != null ? seller.getPhone() : "");
        order.setRemark("基于求购单#" + wanted.getId() + "生成供给订单");
        order.setStatus(1);
        order.setBuyerConfirmed(0);
        order.setSellerConfirmed(0);
        order.setBuyerReviewed(0);
        order.setSellerReviewed(0);
        orderMapper.insert(order);
        int tradingAffected = wantedMapper.markWantedTrading(wanted.getId());
        if (tradingAffected < 1) {
            throw new RuntimeException("求购状态更新失败");
        }
        return getOrderDetail(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO confirmOrder(Long orderId, String sellerPhone) {
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

        // 更新订单状态和卖家电话
        order.setStatus(2); // 已确认、待交易
        order.setConfirmedAt(LocalDateTime.now());
        if (sellerPhone != null && !sellerPhone.trim().isEmpty()) {
            order.setSellerPhone(sellerPhone);
        }
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
            LocalDateTime completedTime = LocalDateTime.now();
            order.setStatus(3);
            order.setCompletedAt(completedTime);
            
            // 更新书籍状态为已售出
            if (order.getBookId() != null && order.getBookId() > 0) {
                bookMapper.updateBookStatusToSold(order.getBookId(), completedTime);
            }
            if (order.getWantedId() != null && order.getWantedId() > 0) {
                wantedMapper.markWantedTraded(order.getWantedId(), "求购已交易");
            }
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
        if (order.getBuyerConfirmed() == null || order.getBuyerConfirmed() != 1) {
            throw new RuntimeException("需买家先确认收货后，卖家才能确认交货");
        }

        // 卖家确认交货
        order.setSellerConfirmed(1);
        order.setSellerConfirmedAt(LocalDateTime.now());

        // 检查是否双方都已确认
        if (order.getBuyerConfirmed() == 1) {
            // 双方都确认，订单完成
            LocalDateTime completedTime = LocalDateTime.now();
            order.setStatus(3);
            order.setCompletedAt(completedTime);
            
            // 更新书籍状态为已售出
            if (order.getBookId() != null && order.getBookId() > 0) {
                bookMapper.updateBookStatusToSold(order.getBookId(), completedTime);
            }
            if (order.getWantedId() != null && order.getWantedId() > 0) {
                wantedMapper.markWantedTraded(order.getWantedId(), "求购已交易");
            }
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

        // 将书籍状态恢复为在售
        if (order.getBookId() != null && order.getBookId() > 0) {
            bookMapper.updateBookStatus(order.getBookId(), 1);
        }
        if (order.getWantedId() != null && order.getWantedId() > 0) {
            wantedMapper.restoreWantedToOpen(order.getWantedId());
        }

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
        return orderMapper.selectSellerOrderList(currentUserId);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<OrderVO> getAdminOrderList(Integer pageNum, Integer pageSize, String orderNo) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OrderVO> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        return orderMapper.selectAdminOrderPage(page, orderNo);
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
