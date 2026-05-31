package org.hang.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.hang.shortlink.admin.pojo.dto.req.GroupReqDto;
import org.hang.shortlink.admin.pojo.po.Group;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-18
 */
public interface GroupService extends IService<Group> {

    /**
     * 新增短链接分组
     */
    Boolean addGroup(GroupReqDto reqDto);

    /**
     * 查询分组集合
     */
    List<Group> queryGroup();

    /**
     * 修改短链接分组
     */
    void updateGroup(GroupReqDto reqDto);

    /**
     * 删除短链接分组
     */
    void removeGroup(String gid);

    /**
     * 排序短链接分组
     */
    void sortGroup(List<GroupReqDto> reqDto);

    /**
     * 添加默认分组
     */
    void addDefaultGroup(String username);
}
