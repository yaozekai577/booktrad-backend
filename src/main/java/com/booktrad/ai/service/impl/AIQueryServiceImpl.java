package com.booktrad.ai.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.booktrad.ai.service.AIQueryService;
import com.booktrad.ai.service.QwenService;
import com.booktrad.book.dto.BookQueryDTO;
import com.booktrad.book.mapper.BookMapper;
import com.booktrad.book.vo.BookPageVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI查询服务实现类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@Service
public class AIQueryServiceImpl implements AIQueryService {

    @Autowired
    private QwenService qwenService;
    
    @Autowired
    private BookMapper bookMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public IPage<BookPageVO> queryBooksByNaturalLanguage(String query, Integer pageNum, Integer pageSize) {
        try {
            log.info("开始AI查询，用户输入: {}", query);
            
            // 1. 调用通义千问解析查询意图
            String jsonResult = qwenService.parseQueryIntent(query);
            if (jsonResult == null || jsonResult.isEmpty()) {
                log.warn("AI解析查询意图失败，返回空结果");
                return new Page<>(pageNum, pageSize);
            }
            
            log.info("AI解析结果: {}", jsonResult);
            
            // 2. 解析JSON结果为查询条件
            BookQueryDTO queryDTO = parseQueryResult(jsonResult);
            
            // 3. 使用解析后的条件查询数据库
            Page<BookPageVO> page = new Page<>(pageNum, pageSize);
            IPage<BookPageVO> result = bookMapper.selectBookPage(page, queryDTO);
            
            log.info("AI查询完成，共找到{}条结果", result.getTotal());
            return result;
            
        } catch (Exception e) {
            log.error("AI查询失败: {}", e.getMessage(), e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 解析AI返回的JSON结果为查询条件
     */
    private BookQueryDTO parseQueryResult(String jsonResult) {
        BookQueryDTO queryDTO = new BookQueryDTO();
        
        try {
            // 清理可能的markdown代码块标记
            jsonResult = jsonResult.replaceAll("```json\\s*", "")
                                   .replaceAll("```\\s*", "")
                                   .trim();
            
            JsonNode root = objectMapper.readTree(jsonResult);
            
            // 关键词
            if (root.has("keyword")) {
                queryDTO.setKeyword(root.get("keyword").asText());
            }
            
            // 分类（使用categoryPath进行模糊匹配）
            if (root.has("category")) {
                String category = root.get("category").asText();
                String mappedCategory = mapCategory(category);
                if (mappedCategory != null) {
                    queryDTO.setCategoryPath(mappedCategory);
                }
            }
            
            // 价格范围
            if (root.has("minPrice")) {
                try {
                    queryDTO.setMinPrice(new BigDecimal(root.get("minPrice").asText()));
                } catch (Exception e) {
                    log.warn("解析minPrice失败: {}", root.get("minPrice").asText());
                }
            }
            if (root.has("maxPrice")) {
                try {
                    queryDTO.setMaxPrice(new BigDecimal(root.get("maxPrice").asText()));
                } catch (Exception e) {
                    log.warn("解析maxPrice失败: {}", root.get("maxPrice").asText());
                }
            }
            
            // 成色（BookQueryDTO使用bookConditions列表）
            if (root.has("condition")) {
                try {
                    int condition = root.get("condition").asInt();
                    queryDTO.setBookConditions(java.util.Arrays.asList(condition));
                } catch (Exception e) {
                    log.warn("解析condition失败: {}", root.get("condition"));
                }
            }
            
            // 交易方式
            if (root.has("tradeType")) {
                queryDTO.setTradeType(root.get("tradeType").asText());
            }
            
            log.info("解析后的查询条件: {}", queryDTO);
            
        } catch (Exception e) {
            log.error("解析AI返回的JSON失败: {}", e.getMessage(), e);
        }
        
        return queryDTO;
    }

    /**
     * 映射分类名称到数据库中的分类
     */
    private String mapCategory(String category) {
        if (category == null || category.isEmpty()) {
            return null;
        }
        
        // 将AI返回的分类映射到数据库中的分类
        category = category.toLowerCase();
        
        if (category.contains("教材") || category.contains("textbook")) {
            return "教材";
        } else if (category.contains("小说") || category.contains("fiction") || category.contains("novel")) {
            return "小说";
        } else if (category.contains("技术") || category.contains("tech") || category.contains("programming")) {
            return "技术";
        } else if (category.contains("考试") || category.contains("exam") || category.contains("test")) {
            return "考试";
        } else if (category.contains("文学") || category.contains("literature")) {
            return "文学";
        } else if (category.contains("历史") || category.contains("history")) {
            return "历史";
        } else if (category.contains("科学") || category.contains("science")) {
            return "科学";
        } else if (category.contains("艺术") || category.contains("art")) {
            return "艺术";
        } else if (category.contains("其他") || category.contains("other")) {
            return "其他";
        }
        
        // 如果无法映射，返回原值
        return category;
    }
}
