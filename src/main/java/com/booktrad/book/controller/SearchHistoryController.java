package com.booktrad.book.controller;

import com.booktrad.book.service.SearchHistoryService;
import com.booktrad.common.context.UserContext;
import com.booktrad.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索历史控制器
 */
@RestController
@RequestMapping("/search-history")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService searchHistoryService;

    /**
     * 添加搜索历史
     * @param keyword 搜索关键词
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Void> addSearchHistory(@RequestParam String keyword) {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            searchHistoryService.addSearchHistory(keyword, userId);
        }
        return Result.success();
    }

    /**
     * 获取当前用户的搜索历史
     * @param limit 限制数量，默认10条
     * @return 搜索历史列表
     */
    @GetMapping("/list")
    public Result<List<String>> getSearchHistory(@RequestParam(defaultValue = "10") int limit) {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            List<String> history = searchHistoryService.getSearchHistory(userId, limit);
            return Result.success(history);
        }
        // 未登录则返回空列表
        return Result.success(List.of());
    }

    /**
     * 清空当前用户的搜索历史
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result<Void> clearSearchHistory() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            searchHistoryService.clearSearchHistory(userId);
        }
        return Result.success();
    }
}
