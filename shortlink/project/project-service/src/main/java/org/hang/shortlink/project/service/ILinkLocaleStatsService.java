package org.hang.shortlink.project.service;

import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsLocaleCNResponseDto;
import org.hang.shortlink.project.pojo.po.LinkLocaleStats;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 短链接访问地区信息表 服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
public interface ILinkLocaleStatsService extends IService<LinkLocaleStats> {

    /**
     * 保存短链接访问地区信息
     * @param fullShortUrl 完整的短链接
     */
    void updateLinkLocaleStats(String fullShortUrl, String ip);

    /**
     * 获取短链接访问地区信息
     * @param fullShortUrl 完整的短链接
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接访问地区信息
     */
    List<ShortLinkStatsLocaleCNResponseDto> getLocaleCnStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接访问地区信息
     * @param fullShortUrls 完整的短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接访问地区信息
     */
    List<ShortLinkStatsLocaleCNResponseDto> getLocaleCnStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

}
