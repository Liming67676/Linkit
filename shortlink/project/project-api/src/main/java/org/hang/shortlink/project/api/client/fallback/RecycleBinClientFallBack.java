package org.hang.shortlink.project.api.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.RecycleBinClient;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description  回收站服务降级
 */
@Slf4j
public class RecycleBinClientFallBack implements FallbackFactory<RecycleBinClient> {
    @Override
    public RecycleBinClient create(Throwable cause) {
        log.error("[回收站服务降级] 服务不可用，异常信息：", cause);
        return new RecycleBinClient() {

            @Override
            public Result saveRecycleBin(RecycleBinSaveRequestDto requestParam) {
                log.warn("[回收站降级] 保存回收站不可用，参数：{}", requestParam);
                return Result.failure("回收站服务暂时不可用，请稍后重试");
            }

            @Override
            public Result<PageResult<RecycleBinLinkPageResponseDto>> pageShortLinkRecycleBin(ShortLinkRecycleBinPageQueryDTO requestParam) {
                log.warn("[回收站降级] 分页查询不可用，返回空数据");
                return Result.success(new PageResult<>());
            }

            @Override
            public Result recoverRecycleBin(RecycleBinRecoverRequestDto recoverRequest) {
                log.warn("[回收站降级] 恢复链接不可用，参数：{}", recoverRequest);
                return Result.failure("回收站服务暂时不可用，请稍后重试");
            }

            @Override
            public Result removeRecycleBin(RecycleBinRemoveRequestDto removeRequest) {
                log.warn("[回收站降级] 删除链接不可用，参数：{}", removeRequest);
                return Result.failure("回收站服务暂时不可用，请稍后重试");
            }
        };
    }
}
