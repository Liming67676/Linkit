package org.hang.shortlink.project.api.client;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.fallback.RecycleBinClientFallBack;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链接回收站客户端服务
 */
@FeignClient(value = "shortlink-project-service",
        contextId = "recycleBinClient",// 唯一标识
        fallbackFactory = RecycleBinClientFallBack.class)
public interface RecycleBinClient {
    /**
     * 短链接保存回收站
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveRequestDto requestParam);

    /**
     * 分页查询回收站短链接
     * 找到gidList中回收链接
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<PageResult<RecycleBinLinkPageResponseDto>> pageShortLinkRecycleBin(@SpringQueryMap ShortLinkRecycleBinPageQueryDTO requestParam);

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverRequestDto recoverRequest);


    /**
     * 移除短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveRequestDto removeRequest);
}
