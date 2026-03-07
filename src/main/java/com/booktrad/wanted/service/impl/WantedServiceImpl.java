package com.booktrad.wanted.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.booktrad.common.context.UserContext;
import com.booktrad.wanted.dto.WantedCloseDTO;
import com.booktrad.wanted.dto.WantedPublishDTO;
import com.booktrad.wanted.dto.WantedQueryDTO;
import com.booktrad.wanted.dto.WantedUpdateDTO;
import com.booktrad.wanted.entity.WantedRequest;
import com.booktrad.wanted.mapper.WantedMapper;
import com.booktrad.wanted.service.WantedService;
import com.booktrad.wanted.vo.WantedDetailVO;
import com.booktrad.wanted.vo.WantedMatchBookVO;
import com.booktrad.wanted.vo.WantedPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 求购模块业务实现，包含发布、修改、关闭、删除与匹配查询逻辑。
 */
@Service
@RequiredArgsConstructor
public class WantedServiceImpl implements WantedService {

    private final WantedMapper wantedMapper;

    /**
     * 求购广场分页，支持关键词、分类、状态与排序条件。
     */
    @Override
    public IPage<WantedPageVO> getWantedPage(WantedQueryDTO queryDTO) {
        Integer page = queryDTO.getPage() == null || queryDTO.getPage() < 1 ? 1 : queryDTO.getPage();
        Integer size = queryDTO.getSize() == null || queryDTO.getSize() < 1 ? 6 : queryDTO.getSize();
        Long currentUserId = UserContext.getUserId();
        Page<WantedPageVO> pageParam = new Page<>(page, size);
        return wantedMapper.selectWantedPage(pageParam, queryDTO, currentUserId);
    }

    /**
     * 我的求购分页，仅返回当前登录用户发布的数据。
     */
    @Override
    public IPage<WantedPageVO> getMyWantedPage(WantedQueryDTO queryDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        Integer page = queryDTO.getPage() == null || queryDTO.getPage() < 1 ? 1 : queryDTO.getPage();
        Integer size = queryDTO.getSize() == null || queryDTO.getSize() < 1 ? 6 : queryDTO.getSize();
        Page<WantedPageVO> pageParam = new Page<>(page, size);
        return wantedMapper.selectMyWantedPage(pageParam, userId, queryDTO);
    }

    /**
     * 获取求购详情，未登录时也可查询但会按SQL规则控制敏感字段返回。
     */
    @Override
    public WantedDetailVO getWantedDetail(Long id) {
        Long currentUserId = UserContext.getUserId();
        WantedDetailVO detailVO = wantedMapper.selectWantedDetailById(id, currentUserId);
        if (detailVO == null) {
            throw new RuntimeException("求购信息不存在");
        }
        return detailVO;
    }

    /**
     * 发布求购，校验登录态和关键字段后落库。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WantedDetailVO publishWanted(WantedPublishDTO publishDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        if (publishDTO.getTitle() == null || publishDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("求购书名不能为空");
        }
        if (publishDTO.getCategoryId() == null) {
            throw new RuntimeException("请选择分类");
        }
        if (publishDTO.getBudget() == null || publishDTO.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("预算必须大于0");
        }

        WantedRequest wantedRequest = new WantedRequest();
        wantedRequest.setBuyerId(userId);
        wantedRequest.setTitle(publishDTO.getTitle().trim());
        wantedRequest.setAuthor(publishDTO.getAuthor());
        wantedRequest.setCategoryId(publishDTO.getCategoryId());
        wantedRequest.setBudget(publishDTO.getBudget());
        wantedRequest.setDesiredCondition(publishDTO.getDesiredCondition());
        wantedRequest.setExpectedLocation(publishDTO.getExpectedLocation());
        wantedRequest.setDescription(publishDTO.getDescription());
        wantedRequest.setContactPhone(publishDTO.getContactPhone());
        wantedRequest.setStatus(1);

        wantedMapper.insert(wantedRequest);
        return getWantedDetail(wantedRequest.getId());
    }

    /**
     * 修改求购，仅发布人可修改，且已关闭求购不可编辑。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WantedDetailVO updateWanted(WantedUpdateDTO updateDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        if (updateDTO == null || updateDTO.getId() == null) {
            throw new RuntimeException("求购ID不能为空");
        }
        if (updateDTO.getTitle() == null || updateDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("求购书名不能为空");
        }
        if (updateDTO.getCategoryId() == null) {
            throw new RuntimeException("请选择分类");
        }
        if (updateDTO.getBudget() == null || updateDTO.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("预算必须大于0");
        }

        WantedRequest existed = wantedMapper.selectWantedEntityById(updateDTO.getId());
        if (existed == null || existed.getDeletedAt() != null) {
            throw new RuntimeException("求购信息不存在");
        }
        if (!userId.equals(existed.getBuyerId())) {
            throw new RuntimeException("只有发布人可以修改求购");
        }
        if (existed.getStatus() != null && existed.getStatus() == 2) {
            throw new RuntimeException("已关闭的求购不允许修改");
        }

        existed.setTitle(updateDTO.getTitle().trim());
        existed.setAuthor(updateDTO.getAuthor());
        existed.setCategoryId(updateDTO.getCategoryId());
        existed.setBudget(updateDTO.getBudget());
        existed.setDesiredCondition(updateDTO.getDesiredCondition());
        existed.setExpectedLocation(updateDTO.getExpectedLocation());
        existed.setDescription(updateDTO.getDescription());
        existed.setContactPhone(updateDTO.getContactPhone());
        wantedMapper.updateById(existed);
        return getWantedDetail(existed.getId());
    }

    /**
     * 关闭求购，仅发布人可执行。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WantedDetailVO closeWanted(Long id, WantedCloseDTO closeDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        WantedRequest wantedRequest = wantedMapper.selectWantedEntityById(id);
        if (wantedRequest == null || wantedRequest.getDeletedAt() != null) {
            throw new RuntimeException("求购信息不存在");
        }
        if (!userId.equals(wantedRequest.getBuyerId())) {
            throw new RuntimeException("只有发布人可以关闭求购");
        }
        if (wantedRequest.getStatus() != null && wantedRequest.getStatus() == 2) {
            throw new RuntimeException("该求购已经关闭");
        }
        String closeReason = closeDTO == null ? null : closeDTO.getCloseReason();
        int affected = wantedMapper.closeWanted(id, userId, closeReason);
        if (affected < 1) {
            throw new RuntimeException("关闭求购失败");
        }
        return getWantedDetail(id);
    }

    /**
     * 删除求购，采用软删除方式，仅发布人可执行。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWanted(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("未授权，请先登录");
        }
        WantedRequest wantedRequest = wantedMapper.selectWantedEntityById(id);
        if (wantedRequest == null || wantedRequest.getDeletedAt() != null) {
            throw new RuntimeException("求购信息不存在");
        }
        if (!userId.equals(wantedRequest.getBuyerId())) {
            throw new RuntimeException("只有发布人可以删除求购");
        }
        int affected = wantedMapper.softDeleteWanted(id, userId);
        if (affected < 1) {
            throw new RuntimeException("删除求购失败");
        }
    }

    /**
     * 查询匹配书籍，按求购预算、成色、分类和关键词匹配在售图书。
     */
    @Override
    public List<WantedMatchBookVO> getMatchBooks(Long wantedId, Integer limit) {
        if (wantedId == null) {
            throw new RuntimeException("求购ID不能为空");
        }
        int finalLimit = (limit == null || limit < 1) ? 6 : Math.min(limit, 20);
        Long currentUserId = UserContext.getUserId();
        WantedRequest wantedRequest = wantedMapper.selectWantedEntityById(wantedId);
        if (wantedRequest == null || wantedRequest.getDeletedAt() != null) {
            throw new RuntimeException("求购信息不存在");
        }
        if (wantedRequest.getStatus() != null
                && wantedRequest.getStatus() == 2
                && (currentUserId == null || !currentUserId.equals(wantedRequest.getBuyerId()))) {
            throw new RuntimeException("求购信息不存在");
        }
        return wantedMapper.selectMatchBooks(wantedId, finalLimit);
    }
}
