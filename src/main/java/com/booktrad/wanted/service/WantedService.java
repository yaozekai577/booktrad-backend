package com.booktrad.wanted.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.wanted.dto.WantedCloseDTO;
import com.booktrad.wanted.dto.WantedPublishDTO;
import com.booktrad.wanted.dto.WantedQueryDTO;
import com.booktrad.wanted.dto.WantedUpdateDTO;
import com.booktrad.wanted.vo.WantedDetailVO;
import com.booktrad.wanted.vo.WantedMatchBookVO;
import com.booktrad.wanted.vo.WantedPageVO;

import java.util.List;

public interface WantedService {

    IPage<WantedPageVO> getWantedPage(WantedQueryDTO queryDTO);

    IPage<WantedPageVO> getMyWantedPage(WantedQueryDTO queryDTO);

    WantedDetailVO getWantedDetail(Long id);

    WantedDetailVO publishWanted(WantedPublishDTO publishDTO);

    WantedDetailVO updateWanted(WantedUpdateDTO updateDTO);

    WantedDetailVO closeWanted(Long id, WantedCloseDTO closeDTO);

    void deleteWanted(Long id);

    List<WantedMatchBookVO> getMatchBooks(Long wantedId, Integer limit);
}
