package com.booktrad.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.order.entity.BookOrder;
import com.booktrad.order.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 订单Mapper
 * @Date 2026/1/24
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Mapper
public interface OrderMapper extends BaseMapper<BookOrder> {

    /**
     * 查询订单详情（带用户和书籍信息）
     */
    OrderVO selectOrderDetailById(@Param("orderId") Long orderId);

    /**
     * 查询用户的订单列表（买家或卖家）
     */
    List<OrderVO> selectOrderListByUserId(@Param("userId") Long userId);

    /**
     * 查询买家的订单列表
     */
    List<OrderVO> selectBuyerOrderList(@Param("buyerId") Long buyerId);

    /**
     * 查询卖家的订单列表
     */
    List<OrderVO> selectSellerOrderList(@Param("sellerId") Long sellerId);
}
