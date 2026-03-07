package com.booktrad.wanted.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.booktrad.common.result.Result;
import com.booktrad.wanted.dto.WantedCloseDTO;
import com.booktrad.wanted.dto.WantedPublishDTO;
import com.booktrad.wanted.dto.WantedQueryDTO;
import com.booktrad.wanted.dto.WantedUpdateDTO;
import com.booktrad.wanted.service.WantedService;
import com.booktrad.wanted.vo.WantedDetailVO;
import com.booktrad.wanted.vo.WantedMatchBookVO;
import com.booktrad.wanted.vo.WantedPageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 求购模块控制器，负责接收前端请求并返回统一结果。
 * @Date 2026/2/20
 * Copyright (C) 2025-2026 All Right Reserved
 * 注意：本内容为个人毕设
 */
@RestController
@RequestMapping("/api/wanted")
@RequiredArgsConstructor
public class WantedController {

    private final WantedService wantedService;


    /**
     * 求购广场分页查询。
     * @param queryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<WantedPageVO>> getWantedPage(WantedQueryDTO queryDTO) {
        try {
            return Result.success(wantedService.getWantedPage(queryDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 当前登录用户的求购分页查询。
     */
    @GetMapping("/my/page")
    public Result<IPage<WantedPageVO>> getMyWantedPage(WantedQueryDTO queryDTO) {
        try {
            return Result.success(wantedService.getMyWantedPage(queryDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取求购详情。
     */
    @GetMapping("/{id}")
    public Result<WantedDetailVO> getWantedDetail(@PathVariable Long id) {
        try {
            return Result.success(wantedService.getWantedDetail(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发布求购信息。
     */
    @PostMapping("/publish")
    public Result<WantedDetailVO> publishWanted(@RequestBody WantedPublishDTO publishDTO) {
        try {
            return Result.success("发布成功", wantedService.publishWanted(publishDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改求购信息。
     */
    @PutMapping("/update")
    public Result<WantedDetailVO> updateWanted(@RequestBody WantedUpdateDTO updateDTO) {
        try {
            return Result.success("修改成功", wantedService.updateWanted(updateDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 关闭求购信息。
     */
    @PutMapping("/{id}/close")
    public Result<WantedDetailVO> closeWanted(@PathVariable Long id, @RequestBody(required = false) WantedCloseDTO closeDTO) {
        try {
            return Result.success("关闭成功", wantedService.closeWanted(id, closeDTO));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除求购信息（软删除）。
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteWanted(@PathVariable Long id) {
        try {
            wantedService.deleteWanted(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询与指定求购匹配的在售书籍。
     */
    @GetMapping("/{id}/match-books")
    public Result<List<WantedMatchBookVO>> getMatchBooks(@PathVariable("id") Long wantedId,
                                                          @RequestParam(required = false) Integer limit) {
        try {
            return Result.success(wantedService.getMatchBooks(wantedId, limit));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
