package org.hang.shortlink.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.ShortLinkStatsClient;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "shortLinkStatsControllerByAdmin")
@RequiredArgsConstructor
@Tag(name = "短链接监控(后台)", description = "短链接统计数据相关操作接口")
public class ShortLinkStatsController {

    private final ShortLinkStatsClient shortLinkStatsClient;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    @Operation(summary = "单个短链接监控数据", description = "获取单个短链接在指定时间内的监控统计数据")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(@ModelAttribute ShortLinkStatsRequestDto requestParam) {
        return shortLinkStatsClient.shortLinkStats(requestParam);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    @Operation(summary = "分组短链接监控数据", description = "获取分组短链接在指定时间内的监控统计数据")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsRequestDto requestParam) {
        return shortLinkStatsClient.groupShortLinkStats(requestParam);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    @Operation(summary = "单个短链接访问记录", description = "获取单个短链接在指定时间内的访问记录监控数据")
    public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordRequestDto requestParam) {
        return shortLinkStatsClient.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    @Operation(summary = "分组短链接访问记录", description = "获取分组短链接在指定时间内的访问记录监控数据")
    public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordRequestDto requestParam) {
        return shortLinkStatsClient.groupShortLinkStatsAccessRecord(requestParam);
    }
}