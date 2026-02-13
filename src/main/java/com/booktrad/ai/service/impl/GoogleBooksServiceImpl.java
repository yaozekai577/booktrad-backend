package com.booktrad.ai.service.impl;

import com.booktrad.ai.dto.BookInfoDTO;
import com.booktrad.ai.service.GoogleBooksService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description Google Books API服务实现类
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    @Value("${ai.google-books.api-url}")
    private String apiUrl;
    
    @Value("${ai.google-books.api-key:}")
    private String apiKey;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public GoogleBooksServiceImpl() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public BookInfoDTO getBookByISBN(String isbn) {
        try {
            // 构建查询URL
            String url = apiUrl + "?q=isbn:" + isbn;
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "&key=" + apiKey;
            }
            
            log.info("查询Google Books API，ISBN: {}", isbn);
            
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonNode root = objectMapper.readTree(jsonData);
                    
                    // 检查是否有结果
                    if (root.has("items") && root.get("items").size() > 0) {
                        JsonNode volumeInfo = root.get("items").get(0).get("volumeInfo");
                        return parseBookInfo(volumeInfo, isbn);
                    } else {
                        log.warn("未找到ISBN为{}的书籍信息", isbn);
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询Google Books API失败: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public BookInfoDTO getBookByTitle(String title) {
        try {
            // 构建查询URL
            String url = apiUrl + "?q=intitle:" + title + "&maxResults=1";
            if (apiKey != null && !apiKey.isEmpty()) {
                url += "&key=" + apiKey;
            }
            
            log.info("查询Google Books API，书名: {}", title);
            
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonNode root = objectMapper.readTree(jsonData);
                    
                    if (root.has("items") && root.get("items").size() > 0) {
                        JsonNode volumeInfo = root.get("items").get(0).get("volumeInfo");
                        return parseBookInfo(volumeInfo, null);
                    } else {
                        log.warn("未找到书名为{}的书籍信息", title);
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询Google Books API失败: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 解析Google Books API返回的书籍信息
     */
    private BookInfoDTO parseBookInfo(JsonNode volumeInfo, String isbn) {
        BookInfoDTO bookInfo = new BookInfoDTO();
        
        // 书名
        if (volumeInfo.has("title")) {
            bookInfo.setTitle(volumeInfo.get("title").asText());
        }
        
        // 作者（取第一个）
        if (volumeInfo.has("authors") && volumeInfo.get("authors").size() > 0) {
            bookInfo.setAuthor(volumeInfo.get("authors").get(0).asText());
        }
        
        // 出版社
        if (volumeInfo.has("publisher")) {
            bookInfo.setPublisher(volumeInfo.get("publisher").asText());
        }
        
        // 出版日期
        if (volumeInfo.has("publishedDate")) {
            bookInfo.setPublishDate(volumeInfo.get("publishedDate").asText());
        }
        
        // ISBN
        if (isbn != null) {
            bookInfo.setIsbn(isbn);
        } else if (volumeInfo.has("industryIdentifiers")) {
            JsonNode identifiers = volumeInfo.get("industryIdentifiers");
            for (JsonNode identifier : identifiers) {
                if ("ISBN_13".equals(identifier.get("type").asText())) {
                    bookInfo.setIsbn(identifier.get("identifier").asText());
                    break;
                }
            }
        }
        
        // 描述
        if (volumeInfo.has("description")) {
            bookInfo.setDescription(volumeInfo.get("description").asText());
        }
        
        // 封面图片
        if (volumeInfo.has("imageLinks")) {
            JsonNode imageLinks = volumeInfo.get("imageLinks");
            if (imageLinks.has("thumbnail")) {
                bookInfo.setCoverImage(imageLinks.get("thumbnail").asText());
            }
        }
        
        // 分类（取第一个）
        if (volumeInfo.has("categories") && volumeInfo.get("categories").size() > 0) {
            bookInfo.setCategory(volumeInfo.get("categories").get(0).asText());
        }
        
        return bookInfo;
    }
}
