package com.booktrad.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.booktrad.book.entity.SearchHistory;
import com.booktrad.book.mapper.SearchHistoryMapper;
import com.booktrad.book.service.SearchHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索历史服务实现类
 */
@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {

    @Override
    public void addSearchHistory(String keyword, Long userId) {
        if (keyword == null || keyword.trim().isEmpty() || userId == null) {
            return; // 参数无效直接返回
        }
        
        // 删除旧的相同关键词记录（为了更新时间到最新）
        UpdateWrapper<SearchHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).eq("keyword", keyword);
        this.remove(updateWrapper);
        
        // 插入新记录
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setSearchTime(LocalDateTime.now());
        history.setIsDeleted(0);
        this.save(history);
        
        // 限制每个用户的历史记录数量，例如最多保留10条
        QueryWrapper<SearchHistory> countWrapper = new QueryWrapper<>();
        countWrapper.eq("user_id", userId).eq("is_deleted", 0);
        long count = this.count(countWrapper);
        if (count > 10) {
            // 找到最早的记录并删除（物理删除或逻辑删除均可，此处采用物理删除以节省空间）
            QueryWrapper<SearchHistory> earliestWrapper = new QueryWrapper<>();
            earliestWrapper.eq("user_id", userId)
                          .eq("is_deleted", 0)
                          .orderByAsc("search_time")
                          .last("limit 1");
            SearchHistory earliest = this.getOne(earliestWrapper);
            if (earliest != null) {
                this.removeById(earliest.getId());
            }
        }
    }

    @Override
    public List<String> getSearchHistory(Long userId, int limit) {
        if (userId == null) {
            return new ArrayList<>(); // 未登录用户返回空列表
        }
        
        // 查询未删除的记录，按时间倒序排列
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                    .eq("is_deleted", 0)
                    .orderByDesc("search_time")
                    .last("limit " + limit);
                    
        List<SearchHistory> historyList = this.list(queryWrapper);
        
        // 提取关键词并返回
        return historyList.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    @Override
    public void clearSearchHistory(Long userId) {
        if (userId == null) {
            return;
        }
        
        // 逻辑删除该用户的所有搜索历史
        UpdateWrapper<SearchHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId).set("is_deleted", 1);
        this.update(updateWrapper);
    }
}
