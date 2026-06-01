package org.hang.shortlink.project.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.constant.RedisConstant;
import org.hang.shortlink.project.mapper.RecycleBinMapper;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.hang.shortlink.project.pojo.po.Link;
import org.hang.shortlink.project.service.IRecycleBinService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<RecycleBinMapper, Link> implements IRecycleBinService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveRecycleBin(RecycleBinSaveRequestDto requestParam) {
        if(requestParam == null
                || StrUtil.isEmpty(requestParam.getGid())
                || StrUtil.isEmpty(requestParam.getFullShortUrl())){
            throw new ServiceException("加入回收站失败");
        }
        // 1. 移入回收站
        boolean updated = lambdaUpdate().set(Link::getEnableStatus, 1)
                .set(Link::getUpdateTime, LocalDateTime.now())
                .eq( Link::getGid, requestParam.getGid())
                .eq(Link::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(Link::getEnableStatus, 0)
                .update();
        if(!updated){
            throw new ServiceException("加入回收站失败");
        }
        // 2. 删除redis中缓存
        deleteLinkCache(requestParam.getFullShortUrl(), requestParam.getGid());
    }

    @Override
    public PageResult<RecycleBinLinkPageResponseDto> pageShortLinkInRecycleBin(ShortLinkRecycleBinPageQueryDTO requestParam) {
        Page<Link> linkPage = lambdaQuery().eq(Link::getEnableStatus, 1)
                .in(Link::getGid, requestParam.getGidList())
                .orderByDesc(Link::getUpdateTime)
                .page(new Page<>(requestParam.getCurrent(), requestParam.getSize()));
        return PageResult.<RecycleBinLinkPageResponseDto>builder()
                .size(linkPage.getSize())
                .current(linkPage.getCurrent())
                .pages(linkPage.getPages())
                .total(linkPage.getTotal())
                .records(BeanUtil.copyToList(linkPage.getRecords(), RecycleBinLinkPageResponseDto.class))
                .build();
    }

    @Override
    public void recoverRecycleBin(RecycleBinRecoverRequestDto requestParam) {
        if(requestParam == null
                || StrUtil.isEmpty(requestParam.getGid())
                || StrUtil.isEmpty(requestParam.getFullShortUrl())){
            throw new ServiceException("恢复回收站失败");
        }

        // 2. 移出回收站
        boolean updated = lambdaUpdate().set(Link::getEnableStatus, 0)
                .set(Link::getUpdateTime, LocalDateTime.now())
                .eq(Link::getGid, requestParam.getGid())
                .eq(Link::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(Link::getEnableStatus, 1)
                .update();
        if(!updated){
            throw new ServiceException("恢复回收站失败");
        }
        deleteGroupCountCache(requestParam.getGid());

    }

    @Override
    public void removeRecycleBin(RecycleBinRemoveRequestDto requestParam) {
        if(requestParam == null
                || StrUtil.isEmpty(requestParam.getGid())
                || StrUtil.isEmpty(requestParam.getFullShortUrl())){
            throw new ServiceException("删除回收站链接失败");
        }
        // 彻底删除
        boolean removed = remove(new LambdaQueryWrapper<Link>()
                .eq(Link::getGid, requestParam.getGid())
                .eq(Link::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(Link::getEnableStatus, 1));
        if(!removed){
            throw new ServiceException("删除回收站链接失败");
        }
        deleteLinkCache(requestParam.getFullShortUrl(), requestParam.getGid());

    }

    private void deleteLinkCache(String fullShortUrl, String gid) {
        deleteCache(List.of(
                String.format(RedisConstant.FULL_SHORT_URL_KEY, fullShortUrl),
                String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid)
        ));
    }

    private void deleteGroupCountCache(String gid) {
        deleteCache(List.of(String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid)));
    }

    private void deleteCache(List<String> keys) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisTemplate.delete(keys);
                }
            });
        } else {
            redisTemplate.delete(keys);
        }
    }
}
