package com.booktrad.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.ai.dto.AIQueryDTO;
import com.booktrad.ai.dto.AiChatDTO;
import com.booktrad.ai.dto.BookInfoDTO;
import com.booktrad.ai.dto.DescriptionGenerateDTO;
import com.booktrad.ai.service.AIQueryService;
import com.booktrad.ai.service.AiAssistantService;
import com.booktrad.ai.service.GoogleBooksService;
import com.booktrad.ai.service.QwenService;
import com.booktrad.ai.vo.AiChatMessageVO;
import com.booktrad.ai.vo.AiChatResponseVO;
import com.booktrad.ai.vo.AiChatSessionVO;
import com.booktrad.book.vo.BookPageVO;
import com.booktrad.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description AI功能控制器
 * @Date 2026-02-13
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private GoogleBooksService googleBooksService;
    
    @Autowired
    private QwenService qwenService;
    
    @Autowired
    private AIQueryService aiQueryService;
    
    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 根据ISBN获取书籍信息（智能补全）
     * 
     * @param isbn ISBN号
     * @return 书籍信息
     */
    @GetMapping("/book/isbn/{isbn}")
    public Result<BookInfoDTO> getBookByISBN(@PathVariable String isbn) {
        try {
            log.info("收到ISBN查询请求: {}", isbn);
            
            if (isbn == null || isbn.trim().isEmpty()) {
                return Result.error("ISBN不能为空");
            }
            
            BookInfoDTO bookInfo = googleBooksService.getBookByISBN(isbn);
            
            if (bookInfo == null) {
                return Result.error("未找到该书籍信息，请检查ISBN是否正确");
            }
            
            return Result.success(bookInfo);
            
        } catch (Exception e) {
            log.error("查询书籍信息失败: {}", e.getMessage(), e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据书名搜索书籍信息（智能补全）
     * 
     * @param title 书名
     * @return 书籍信息
     */
    @GetMapping("/book/title")
    public Result<BookInfoDTO> getBookByTitle(@RequestParam String title) {
        try {
            log.info("收到书名查询请求: {}", title);
            
            if (title == null || title.trim().isEmpty()) {
                return Result.error("书名不能为空");
            }
            
            BookInfoDTO bookInfo = googleBooksService.getBookByTitle(title);
            
            if (bookInfo == null) {
                return Result.error("未找到该书籍信息，请尝试其他关键词");
            }
            
            return Result.success(bookInfo);
            
        } catch (Exception e) {
            log.error("搜索书籍信息失败: {}", e.getMessage(), e);
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    /**
     * AI生成书籍描述
     * 
     * @param dto 书籍信息DTO
     * @return 生成的描述
     */
    @PostMapping("/description/generate")
    public Result<String> generateDescription(@RequestBody DescriptionGenerateDTO dto) {
        try {
            log.info("收到生成描述请求，书名: {}", dto.getTitle());
            
            // 参数校验
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                return Result.error("书名不能为空");
            }
            if (dto.getAuthor() == null || dto.getAuthor().trim().isEmpty()) {
                return Result.error("作者不能为空");
            }
            if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
                return Result.error("分类不能为空");
            }
            if (dto.getCondition() == null) {
                return Result.error("成色不能为空");
            }
            
            String description = qwenService.generateBookDescription(
                dto.getTitle(), 
                dto.getAuthor(), 
                dto.getCategory(), 
                dto.getCondition()
            );
            
            if (description == null || description.isEmpty()) {
                return Result.error("生成失败，请稍后重试或手动填写描述");
            }
            
            return Result.success(description);
            
        } catch (Exception e) {
            log.error("生成描述失败: {}", e.getMessage(), e);
            return Result.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * AI自然语言查询书籍
     * 
     * @param dto 查询DTO
     * @return 分页结果
     */
    @PostMapping("/query")
    public Result<IPage<BookPageVO>> queryBooks(@RequestBody AIQueryDTO dto) {
        try {
            log.info("收到AI查询请求: {}", dto.getQuery());
            
            if (dto.getQuery() == null || dto.getQuery().trim().isEmpty()) {
                return Result.error("查询内容不能为空");
            }
            
            // 设置默认分页参数
            if (dto.getPageNum() == null || dto.getPageNum() < 1) {
                dto.setPageNum(1);
            }
            if (dto.getPageSize() == null || dto.getPageSize() < 1) {
                dto.setPageSize(10);
            }
            
            IPage<BookPageVO> result = aiQueryService.queryBooksByNaturalLanguage(
                dto.getQuery(), 
                dto.getPageNum(), 
                dto.getPageSize()
            );
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("AI查询失败: {}", e.getMessage(), e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * AI助手聊天
     * 
     * @param dto 聊天请求DTO
     * @return AI回复
     */
    @PostMapping("/assistant/chat")
    public Result<AiChatResponseVO> assistantChat(@RequestBody AiChatDTO dto) {
        try {
            log.info("收到AI助手聊天请求，sessionId: {}, message: {}", dto.getSessionId(), dto.getMessage());
            
            if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
                return Result.error("消息内容不能为空");
            }
            
            AiChatResponseVO response = aiAssistantService.chat(dto.getSessionId(), dto.getMessage());
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("AI助手聊天失败: {}", e.getMessage(), e);
            return Result.error("聊天失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI助手会话列表
     * 
     * @return 会话列表
     */
    @GetMapping("/assistant/sessions")
    public Result<java.util.List<AiChatSessionVO>> getAssistantSessions() {
        try {
            log.info("获取AI助手会话列表");
            
            java.util.List<AiChatSessionVO> sessions = aiAssistantService.getSessionList();
            
            return Result.success(sessions);
            
        } catch (Exception e) {
            log.error("获取会话列表失败: {}", e.getMessage(), e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取会话的历史消息
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/assistant/sessions/{sessionId}/messages")
    public Result<java.util.List<AiChatMessageVO>> getSessionMessages(@PathVariable Long sessionId) {
        try {
            log.info("获取会话历史消息，sessionId: {}", sessionId);
            
            java.util.List<AiChatMessageVO> messages = aiAssistantService.getSessionMessages(sessionId);
            
            return Result.success(messages);
            
        } catch (Exception e) {
            log.error("获取历史消息失败: {}", e.getMessage(), e);
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 创建新的AI助手会话
     * 
     * @return 新会话
     */
    @PostMapping("/assistant/sessions")
    public Result<AiChatSessionVO> createAssistantSession() {
        try {
            log.info("创建新的AI助手会话");
            
            AiChatSessionVO session = aiAssistantService.createSession();
            
            return Result.success(session);
            
        } catch (Exception e) {
            log.error("创建会话失败: {}", e.getMessage(), e);
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 删除AI助手会话
     * 
     * @param sessionId 会话ID
     * @return 成功标识
     */
    @DeleteMapping("/assistant/sessions/{sessionId}")
    public Result<Void> deleteAssistantSession(@PathVariable Long sessionId) {
        try {
            log.info("删除AI助手会话，sessionId: {}", sessionId);
            
            aiAssistantService.deleteSession(sessionId);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("删除会话失败: {}", e.getMessage(), e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
