package org.hang.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hang.shortlink.project.pojo.po.LinkLocaleStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStats> {

    /**
     * 单个短链接：按省份/城市分组查汇总+占比
     */
    List<Map<String, Object>> selectProvinceStatsWithRatio(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 批量短链接：按省份/城市分组查汇总+占比
     */
    List<Map<String, Object>> selectLocaleStatsWithRatioBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

}
