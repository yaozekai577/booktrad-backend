package com.booktrad.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.booktrad.book.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 搜索历史Mapper接口
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {
}
