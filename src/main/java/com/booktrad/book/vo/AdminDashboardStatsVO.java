package com.booktrad.book.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 管理员仪表盘统计数据VO
 * @Date 2026/03/14
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Data
public class AdminDashboardStatsVO {
    // 基础统计
    private Integer totalUsers;          // 总用户数
    private Integer totalBooks;          // 总书籍数
    private Integer totalOrders;         // 总订单数
    private java.math.BigDecimal totalTransactionAmount; // 总交易额

    // 书籍状态分布
    private Map<String, Integer> bookStatusDistribution; // 在售、已售、下架等数量

    // 订单状态分布
    private Map<String, Integer> orderStatusDistribution; // 待确认、进行中、已完成、已取消等数量

    // 最近7天趋势（日期 -> 数量）
    private List<Map<String, Object>> dailyNewBooks;    // 每日新增书籍
    private List<Map<String, Object>> dailyNewOrders;   // 每日新增订单
    private List<Map<String, Object>> dailyNewUsers;    // 每日新增用户
}
