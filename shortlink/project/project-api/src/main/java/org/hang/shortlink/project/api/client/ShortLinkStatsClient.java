package org.hang.shortlink.project.api.client;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.fallback.ShortLinkClientFallBack;
import org.hang.shortlink.project.api.client.fallback.ShortLinkStatsClientFallBack;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链统计客户端
 */

@FeignClient(value = "shortlink-project-service",
        contextId = "shortLinkStatsClient", // 唯一标识
        fallbackFactory = ShortLinkStatsClientFallBack.class)
public interface ShortLinkStatsClient {

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> shortLinkStats(@SpringQueryMap ShortLinkStatsRequestDto statsRequest);

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsRequestDto groupStatsRequest);

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> shortLinkStatsAccessRecord(@SpringQueryMap ShortLinkStatsAccessRecordRequestDto accessRecordRequest);


    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> groupShortLinkStatsAccessRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordRequestDto groupStatsAccessRecordRequest);
}
