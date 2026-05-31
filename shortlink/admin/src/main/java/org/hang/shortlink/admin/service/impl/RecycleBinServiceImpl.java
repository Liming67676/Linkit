package org.hang.shortlink.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.pojo.po.Group;
import org.hang.shortlink.admin.service.GroupService;
import org.hang.shortlink.admin.service.RecycleBinService;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.utils.UserContext;
import org.hang.shortlink.project.api.client.RecycleBinClient;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Service(value = "recycleBinServiceImplByAdmin")
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final RecycleBinClient recycleBinClient;

    private final GroupService groupService;

    @Override
    public void saveRecycleBin(RecycleBinSaveRequestDto requestParam) {
        recycleBinClient.saveRecycleBin(requestParam);
    }

    @Override
    public Result<PageResult<RecycleBinLinkPageResponseDto>> pageRecycleBinShortLink(ShortLinkRecycleBinPageQueryDTO requestParam) {
       // 1. 查找用户分组
        String userInfo = UserContext.getUserInfo();
        if(StrUtil.isEmpty(userInfo)){
            throw new RuntimeException("用户不存在");
        }
        List<Group> groups = groupService.lambdaQuery().eq(Group::getUsername, userInfo).list();
        if (groups.isEmpty()) {
            throw new RuntimeException("用户分组不存在");
        }
        // 2. 通过组别gid就可以判定这些组别中的短链接是否过期
        List<String> gidList = groups.stream().map(Group::getGid).toList();
        requestParam.setGidList(gidList);
        return recycleBinClient.pageShortLinkRecycleBin(requestParam);
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverRequestDto requestParam) {
        recycleBinClient.recoverRecycleBin(requestParam);
    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveRequestDto requestParam) {
        recycleBinClient.removeRecycleBin(requestParam);
    }
}
