package org.hang.shortlink.project.api.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.ShortLinkStatsClient;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsRespDTO;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 回收站服务降级
 */
@Slf4j
public class ShortLinkStatsClientFallBack implements FallbackFactory<ShortLinkStatsClient> {
    @Override
    public ShortLinkStatsClient create(Throwable cause) {
        log.error("[短链接统计服务降级] 服务不可用，异常信息：", cause);
        return new ShortLinkStatsClient() {

            /**
             * 1. 单个短链接监控数据【查询型接口】
             * 降级策略：返回空数据，前端友好展示
             */
            @Override
            public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsRequestDto statsRequest) {
                log.warn("[短链接统计降级] 单个短链接监控数据不可用，返回空");
                return Result.success(new ShortLinkStatsRespDTO());
            }

            /**
             * 2. 分组短链接监控数据【查询型接口】
             * 降级策略：返回空数据，前端友好展示
             */
            @Override
            public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsRequestDto groupStatsRequest) {
                log.warn("[短链接统计降级] 分组短链接监控数据不可用，返回空");
                return Result.success(new ShortLinkStatsRespDTO());
            }

            /**
             * 3. 单个短链接访问记录【查询型接口】
             * 降级策略：返回空分页数据，前端友好展示
             */
            @Override
            public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordRequestDto accessRecordRequest) {
                log.warn("[短链接统计降级] 单个短链接访问记录不可用，返回空分页");
                return Result.success(new PageResult<>());
            }

            /**
             * 4. 分组短链接访问记录【查询型接口】
             * 降级策略：返回空分页数据，前端友好展示
             */
            @Override
            public Result<PageResult<ShortLinkStatsAccessRecordResponseDto>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordRequestDto groupStatsAccessRecordRequest) {
                log.warn("[短链接统计降级] 分组短链接访问记录不可用，返回空分页");
                return Result.success(new PageResult<>());
            }
        };
    }
}
