package org.hang.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hang.shortlink.project.pojo.po.LinkBrowserStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStats> {

    List<Map<String, Object>> selectBrowserStatsWithRatio(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Map<String, Object>> selectBrowserStatsWithRatioBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
