package org.hang.shortlink.project.service;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsRespDTO;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链接总监控服务接口
 */
public interface IShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     * @param statsRequest 短链接监控数据请求参数
     * @return 短链接监控数据响应参数
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsRequestDto statsRequest);

    /**
     * 获取分组短链接监控数据
     * @param groupStatsRequest 分组短链接监控数据请求参数
     * @return 分组短链接监控数据响应参数
     */
    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsRequestDto groupStatsRequest);

    /**
     * 获取短链接访问记录
     * @param accessRecordRequest 短链接访问记录请求参数
     * @return 短链接访问记录响应参数
     */
    PageResult<ShortLinkStatsAccessRecordResponseDto> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordRequestDto accessRecordRequest);

    /**
     * 获取分组短链接访问记录
     * @param groupStatsAccessRecordRequest 分组短链接访问记录请求参数
     * @return 分组短链接访问记录响应参数
     */
    PageResult<ShortLinkStatsAccessRecordResponseDto> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordRequestDto groupStatsAccessRecordRequest);
}
