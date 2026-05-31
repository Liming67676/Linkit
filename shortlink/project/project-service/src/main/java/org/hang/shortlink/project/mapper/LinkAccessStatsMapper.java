package org.hang.shortlink.project.mapper;

import org.apache.ibatis.annotations.Param;
import org.hang.shortlink.project.pojo.po.LinkAccessStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 短链接访问基础属性表 Mapper 接口
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStats> {

    // 1. 查询总PV/UV/UIP
    Map<String, Object> selectTotalPvUvUip(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // 2. 按日期分组统计（每日数据）
    List<Map<String, Object>> selectDailyStats(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // 3. 按小时分组统计（0-23）
    List<Map<String, Object>> selectHourStats(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // 4. 按星期分组统计（0-6）
    List<Map<String, Object>> selectWeekdayStats(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // ========== 批量短链接统计方法 ==========
    Map<String, Object> selectTotalPvUvUipBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Map<String, Object>> selectDailyStatsBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Map<String, Object>> selectHourStatsBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Map<String, Object>> selectWeekdayStatsBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 批量查询每个短链接的总PV/UV/UIP + 今日PV/UV/UIP（分页使用）
     */
    List<Map<String, Object>> selectPagePvUvUipStats(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("gid") String gid);

}
