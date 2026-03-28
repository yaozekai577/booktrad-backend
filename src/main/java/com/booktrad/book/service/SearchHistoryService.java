package com.booktrad.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.booktrad.book.entity.SearchHistory;

import java.util.List;

/**
 * 搜索历史服务接口
 */
public interface SearchHistoryService extends IService<SearchHistory> {

    /**
     * 添加搜索历史
     * @param keyword 搜索关键词
     * @param userId 用户ID
     */
    void addSearchHistory(String keyword, Long userId);

    /**
     * 获取用户的搜索历史列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索历史关键词列表
     */
    List<String> getSearchHistory(Long userId, int limit);

    /**
     * 清空用户的搜索历史
     * @param userId 用户ID
     */
    void clearSearchHistory(Long userId);
}
