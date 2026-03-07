package com.booktrad.wanted.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.wanted.dto.WantedQueryDTO;
import com.booktrad.wanted.entity.WantedRequest;
import com.booktrad.wanted.vo.WantedDetailVO;
import com.booktrad.wanted.vo.WantedMatchBookVO;
import com.booktrad.wanted.vo.WantedPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WantedMapper extends BaseMapper<WantedRequest> {

    IPage<WantedPageVO> selectWantedPage(IPage<WantedPageVO> page,
                                         @Param("query") WantedQueryDTO query,
                                         @Param("currentUserId") Long currentUserId);

    IPage<WantedPageVO> selectMyWantedPage(IPage<WantedPageVO> page,
                                           @Param("buyerId") Long buyerId,
                                           @Param("query") WantedQueryDTO query);

    WantedDetailVO selectWantedDetailById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);

    WantedRequest selectWantedEntityById(@Param("id") Long id);

    int closeWanted(@Param("id") Long id, @Param("buyerId") Long buyerId, @Param("closeReason") String closeReason);

    int markWantedTrading(@Param("id") Long id);

    int markWantedTraded(@Param("id") Long id, @Param("closeReason") String closeReason);

    int restoreWantedToOpen(@Param("id") Long id);

    int softDeleteWanted(@Param("id") Long id, @Param("buyerId") Long buyerId);

    List<WantedMatchBookVO> selectMatchBooks(@Param("wantedId") Long wantedId, @Param("limit") Integer limit);
}
