package org.hang.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.hang.shortlink.project.pojo.po.LinkAccessLogStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 短链接全维度访问日志统计表 Mapper 接口
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
public interface LinkAccessLogStatsMapper extends BaseMapper<LinkAccessLogStats> {

    /**
     * 查询高频访问IP Top10
     */
    List<Map<String, Object>> selectTopIpStats(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * uv_type：newUser=新访客 oldUser=老访客
     */
    Map<String, Object> findUvTypeCntByShortLink(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 批量查询高频访问IP Top10
     */
    List<Map<String, Object>> selectTopIpStatsBatch(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 批量查询新老访客统计
     */
    Map<String, Object> findUvTypeCntByShortLinks(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    /**
     * 分页查询访问记录（支持单个/批量短链接）
     */
    IPage<LinkAccessLogStats> selectAccessRecordPage(
            Page<LinkAccessLogStats> page,
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * 批量查询指定IP在查询开始前是否有历史访问记录（用于动态判断新老访客）
     * @return 在 start 前有过访问记录的 IP 集合
     */
    List<String> selectExistingIpsBeforeDate(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("ips") List<String> ips,
            @Param("start") LocalDateTime start
    );
}
