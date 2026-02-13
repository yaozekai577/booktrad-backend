package com.booktrad.ai.service.impl;

import com.booktrad.ai.dto.BookInfoDTO;
import com.booktrad.ai.service.GoogleBooksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.booktrad.ai.service.QwenService;

import java.util.concurrent.TimeUnit;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 图书信息API服务实现类（使用AI生成书籍信息）
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    @Autowired
    private QwenService qwenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BookInfoDTO getBookByISBN(String isbn) {
        log.info("使用AI查询ISBN: {}", isbn);
        return getBookInfoByAI(isbn, true);
    }

    @Override
    public BookInfoDTO getBookByTitle(String title) {
        log.info("使用AI查询书名: {}", title);
        return getBookInfoByAI(title, false);
    }

    /**
     * 使用AI获取书籍信息
     */
    private BookInfoDTO getBookInfoByAI(String query, boolean isIsbn) {
        try {
            // 构建AI提示词
            String prompt;
            if (isIsbn) {
                prompt = String.format(
                    "请根据ISBN号 %s 提供这本书的详细信息。\n\n" +
                    "要求：\n" +
                    "1. 如果你知道这本书，请提供准确的信息\n" +
                    "2. 如果不确定，请根据ISBN的出版社代码和书号进行合理推测\n" +
                    "3. 必须返回完整的JSON格式\n\n" +
                    "请严格按照以下JSON格式返回（不要有任何其他文字）：\n" +
                    "{\n" +
                    "  \"title\": \"书名\",\n" +
                    "  \"author\": \"作者名\",\n" +
                    "  \"publisher\": \"出版社名称\",\n" +
                    "  \"publishDate\": \"出版年月（格式：YYYY-MM）\",\n" +
                    "  \"isbn\": \"%s\",\n" +
                    "  \"description\": \"书籍简介（50-100字）\",\n" +
                    "  \"category\": \"分类（从以下选择：教材/小说/技术/考试/文学/历史/科学/艺术/其他）\"\n" +
                    "}\n\n" +
                    "注意：所有字段都必须填写，不能为空。如果某个信息不确定，请填写合理的推测值。",
                    query, query
                );
            } else {
                prompt = String.format(
                    "请根据书名 \"%s\" 提供这本书的详细信息。\n\n" +
                    "要求：\n" +
                    "1. 如果你知道这本书，请提供准确的信息\n" +
                    "2. 如果有多本同名书，选择最知名的一本\n" +
                    "3. 必须返回完整的JSON格式\n\n" +
                    "请严格按照以下JSON格式返回（不要有任何其他文字）：\n" +
                    "{\n" +
                    "  \"title\": \"%s\",\n" +
                    "  \"author\": \"作者名\",\n" +
                    "  \"publisher\": \"出版社名称\",\n" +
                    "  \"publishDate\": \"出版年月（格式：YYYY-MM）\",\n" +
                    "  \"isbn\": \"ISBN号（如果知道）\",\n" +
                    "  \"description\": \"书籍简介（50-100字）\",\n" +
                    "  \"category\": \"分类（从以下选择：教材/小说/技术/考试/文学/历史/科学/艺术/其他）\"\n" +
                    "}\n\n" +
                    "注意：所有字段都必须填写，不能为空。如果某个信息不确定，请填写合理的推测值。",
                    query, query
                );
            }

            log.info("发送给AI的提示词: {}", prompt);

            // 调用通义千问获取书籍信息
            String aiResponse = qwenService.callAI(
                "你是一个专业的图书信息助手，擅长提供准确的书籍信息。请严格按照要求的JSON格式返回数据。",
                prompt
            );
            
            if (aiResponse == null || aiResponse.isEmpty()) {
                log.warn("AI返回结果为空");
                return null;
            }

            log.info("AI返回的原始结果: {}", aiResponse);

            // 解析AI返回的JSON
            BookInfoDTO result = parseAIResponse(aiResponse);
            
            if (result != null) {
                log.info("成功解析书籍信息: title={}, author={}, publisher={}", 
                    result.getTitle(), result.getAuthor(), result.getPublisher());
            } else {
                log.warn("解析AI返回结果失败");
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("使用AI查询书籍信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解析AI返回的书籍信息
     */
    private BookInfoDTO parseAIResponse(String aiResponse) {
        try {
            // 清理可能的markdown代码块标记
            aiResponse = aiResponse.replaceAll("```json\\s*", "")
                                   .replaceAll("```\\s*", "")
                                   .trim();
            
            JsonNode root = objectMapper.readTree(aiResponse);
            
            BookInfoDTO bookInfo = new BookInfoDTO();
            
            if (root.has("title") && !root.get("title").asText().isEmpty()) {
                bookInfo.setTitle(root.get("title").asText());
            }
            if (root.has("author") && !root.get("author").asText().isEmpty()) {
                bookInfo.setAuthor(root.get("author").asText());
            }
            if (root.has("publisher") && !root.get("publisher").asText().isEmpty()) {
                bookInfo.setPublisher(root.get("publisher").asText());
            }
            if (root.has("publishDate") && !root.get("publishDate").asText().isEmpty()) {
                bookInfo.setPublishDate(root.get("publishDate").asText());
            }
            if (root.has("isbn") && !root.get("isbn").asText().isEmpty()) {
                bookInfo.setIsbn(root.get("isbn").asText());
            }
            if (root.has("description") && !root.get("description").asText().isEmpty()) {
                bookInfo.setDescription(root.get("description").asText());
            }
            if (root.has("category") && !root.get("category").asText().isEmpty()) {
                bookInfo.setCategory(root.get("category").asText());
            }
            
            // coverImage不需要了，用户会自己上传
            
            log.info("成功解析AI返回的书籍信息: {}", bookInfo.getTitle());
            return bookInfo;
            
        } catch (Exception e) {
            log.error("解析AI返回的JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
