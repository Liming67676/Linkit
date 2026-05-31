package org.hang.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsRespDTO;
import org.hang.shortlink.project.service.IShortLinkStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 短链接监控控制层
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "短链接监控", description = "短链接统计数据相关操作接口")
public class LinkStatsController {

    private final IShortLinkStatsService shortLinkStatsService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    @Operation(summary = "单个短链接监控数据", description = "获取单个短链接在指定时间内的监控统计数据")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsRequestDto statsRequest) {
        return Result.success(shortLinkStatsService.oneShortLinkStats(statsRequest));
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    @Operation(summary = "分组短链接监控数据", description = "获取分组短链接在指定时间内的监控统计数据")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsRequestDto groupStatsRequest) {
        return Result.success(shortLinkStatsService.groupShortLinkStats(groupStatsRequest));
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    @Operation(summary = "单个短链接访问记录", description = "获取单个短链接在指定时间内的访问记录监控数据")
    public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordRequestDto accessRecordRequest) {
        return Result.success(shortLinkStatsService.shortLinkStatsAccessRecord(accessRecordRequest));
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    @Operation(summary = "分组短链接访问记录", description = "获取分组短链接在指定时间内的访问记录监控数据")
    public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordRequestDto groupStatsAccessRecordRequest) {
        return Result.success(shortLinkStatsService.groupShortLinkStatsAccessRecord(groupStatsAccessRecordRequest));
    }

}
