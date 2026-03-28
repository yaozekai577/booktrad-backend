package com.booktrad.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 搜索历史实体类
 */
@Data
@TableName("search_history")
public class SearchHistory {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（未登录为NULL）
     */
    private Long userId;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 搜索时间
     */
    private LocalDateTime searchTime;
    
    /**
     * 是否删除
     */
    private Integer isDeleted;
}
