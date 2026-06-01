package org.hang.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.mapper.GroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.admin.pojo.dto.req.GroupReqDto;
import org.hang.shortlink.admin.pojo.po.Group;
import org.hang.shortlink.admin.properties.RedisConstant;
import org.hang.shortlink.admin.properties.ShortLinkProperties;
import org.hang.shortlink.admin.service.GroupService;
import org.hang.shortlink.admin.service.ShortLinkActualRemoteService;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.utils.UserContext;
import org.hang.shortlink.admin.utils.UUidV7Generator;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-18
 */
@Service
@EnableConfigurationProperties(ShortLinkProperties.class)
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final UUidV7Generator uUidV7Generator;

    private final ShortLinkProperties shortLinkProperties;

    private final RedissonClient redissonClient;

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;


    @Override
    public Boolean addGroup(GroupReqDto reqDto) {
        if(Objects.isNull(reqDto)){
            throw new ServiceException("分组参数错误");
        }
        String name = reqDto.getName();
        if(name.isBlank()){
            throw new ServiceException("分组参数错误");
        }
        String username = UserContext.getUserInfo();
        if(StrUtil.isBlank(username)){
            throw new ServiceException("请登录后重试");
        }
        // 阻塞锁：防止用户多平台登录请求
        RLock lock = redissonClient.getLock(String.format(RedisConstant.GROUP_ADD_LOCK, username));
        lock.lock();
        try{
            Integer maxGroup = shortLinkProperties.getMaxGroup();
            // 2. 加锁后查询：当前用户分组总数
            Long currentGroupCount = lambdaQuery()
                    .eq(Group::getUsername, username)
                    .count();

            // 3. 判断是否超过最大分组数
            if (currentGroupCount >= maxGroup) {
                throw new ServiceException("分组数量已达上限，最多创建" + maxGroup + "个分组");
            }

            // 4. 校验分组名重复
            Long nameCount = lambdaQuery()
                    .eq(Group::getName, reqDto.getName())
                    .eq(Group::getUsername, username)
                    .count();
            if(!nameCount.equals(0L)){
                throw new ServiceException("分组已存在");
            }

            // 5. 新增分组
            String gid = uUidV7Generator.generate32BitGid();
            Group group = new Group()
                    .setGid(gid)
                    .setName(reqDto.getName())
                    .setSortOrder(0)
                    .setUsername(username);
            boolean saved = save(group);
            if(!saved){
                throw new ServiceException("新增分组失败");
            }

            return true;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public List<Group> queryGroup() {
        String username = UserContext.getUserInfo();
        if (StrUtil.isBlank(username)) {
            throw new ServiceException("请登录后重试");
        }
        List<Group> list = lambdaQuery()
                .eq(Group::getUsername, username)
                .orderByDesc(Group::getSortOrder,Group::getUpdateTime).list();
        if(CollUtil.isEmpty(list)){
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public void updateGroup(GroupReqDto reqDto) {
        if(Objects.isNull(reqDto)){
            throw new ServiceException("更新分组参数错误");
        }
        String gid = reqDto.getGid();
        String name = reqDto.getName();
        Integer sortOrder = reqDto.getSortOrder();
        if(gid.isBlank()){
            throw new ServiceException("更新分组参数错误");
        }
        String username = UserContext.getUserInfo();
        if(StrUtil.isBlank(username)){
            throw new ServiceException("请登录后重试");
        }
        // 同一用户分组名唯一
        if(!name.isBlank()){
            Long count = lambdaQuery().eq(Group::getName, name)
                    .eq(Group::getUsername, username).count();
            if(!count.equals(0L)){
                throw new ServiceException("分组已存在");
            }
        }
        // 更新
        boolean updated = lambdaUpdate()
                .set(Group::getName, name)
                .set(sortOrder != null,Group::getSortOrder,sortOrder)
                .eq(Group::getGid, gid).update();
        if(!updated){
            throw new ServiceException("更新分组失败");
        }
    }

    @Override
    public void removeGroup(String gid) {
        if(StrUtil.isBlank(gid)){
            throw new ServiceException("删除分组失败");
        }
        String username = UserContext.getUserInfo();
        if(StrUtil.isBlank(username)){
            throw new ServiceException("请登录后重试");
        }
        Long groupCount = lambdaQuery()
                .eq(Group::getGid, gid)
                .eq(Group::getUsername, username)
                .count();
        if(groupCount == null || groupCount.equals(0L)){
            throw new ServiceException("删除分组失败");
        }
        Result<List<LinkGroupCountResponseDto>> countResult = shortLinkActualRemoteService.listGroupShortLinkCount(List.of(gid));
        if(countResult == null || !countResult.isSuccess() || CollUtil.isEmpty(countResult.getData())){
            throw new ServiceException("查询分组短链接数量失败");
        }
        Integer shortLinkCount = countResult.getData().stream()
                .filter(each -> gid.equals(each.getGid()))
                .findFirst()
                .map(LinkGroupCountResponseDto::getShortLinkCount)
                .orElse(0);
        if(shortLinkCount > 0){
            throw new ServiceException("该分组下存在短链接，不能删除，请先删除或移动短链接");
        }
        boolean removed = lambdaUpdate()
                .eq(Group::getGid, gid)
                .eq(Group::getUsername, username)
                .remove();
        if(!removed){
            throw new ServiceException("删除分组失败");
        }
    }

    @Override
    public void sortGroup(List<GroupReqDto> reqDto) {
        if(Objects.isNull(reqDto)){
            throw new ServiceException("排序参数错误");
        }
        reqDto.forEach(reqDto1 -> {
            String gid = reqDto1.getGid();
            Integer sortOrder = reqDto1.getSortOrder();
            if(gid.isBlank() || sortOrder == null){
                throw new ServiceException("排序参数错误");
            }
            boolean updated = lambdaUpdate()
                    .set(Group::getSortOrder, sortOrder)
                    .eq(Group::getGid, gid)
                    .eq(Group::getUsername, UserContext.getUserInfo())
                    .update();
            if(!updated){
                throw new ServiceException("排序短链接分组失败");
            }
        });
    }

    @Override
    public void addDefaultGroup(String username) {
        if(StrUtil.isEmpty(username)){
            throw new ServiceException("用户名错误");
        }
        Long count = lambdaQuery()
                .eq(Group::getUsername, username)
                .eq(Group::getName, "default")
                .count();
        if (count != null && count > 0) {
            return;
        }
        String gid = uUidV7Generator.generate32BitGid();
        Group group = Group.builder()
                .name("default")
                .gid(gid)
                .username(username)
                .sortOrder(0)
                .build();
        boolean saved = save(group);
        if(!saved){
            throw new ServiceException("添加默认分组失败");
        }
    }
}
