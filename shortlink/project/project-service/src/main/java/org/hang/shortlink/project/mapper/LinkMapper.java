package org.hang.shortlink.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.hang.shortlink.project.pojo.po.Link;

import java.util.List;

/**
 * <p>
 * 短链接表 Mapper 接口
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-25
 */
@Mapper
public interface LinkMapper extends BaseMapper<Link> {

    /**
     * 根据分组标识查询该分组下所有有效的短链接
     */
    List<String> selectFullShortUrlsByGid(@Param("gid") String gid);

    /**
     * 查询所有不重复的有效分组gid（适配分表）
     */
    @Select("SELECT DISTINCT gid FROM t_link WHERE del_flag = 0")
    List<String> selectDistinctGid();
}
