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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description  回收站服务降级
 */
@Slf4j
public class RecycleBinClientFallBack implements FallbackFactory<RecycleBinClient> {

    private static final String EXCHANGE = "short.link.direct";
    private static final String SAVE_ROUTING_KEY = "short.link.recycle.save";
    private static final String RECOVER_ROUTING_KEY = "short.link.recycle.recover";
    private static final String REMOVE_ROUTING_KEY = "short.link.recycle.remove";

    private final RabbitTemplate rabbitTemplate;

    public RecycleBinClientFallBack(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public RecycleBinClient create(Throwable cause) {
        log.error("[回收站服务降级] 服务不可用，异常信息：", cause);
        return new RecycleBinClient() {

            @Override
            public Result saveRecycleBin(RecycleBinSaveRequestDto requestParam) {
                log.warn("[回收站降级] 保存回收站失败，触发异步补偿，参数：{}", requestParam);
                rabbitTemplate.convertAndSend(EXCHANGE, SAVE_ROUTING_KEY, requestParam);
                return Result.success("操作已提交，系统处理中...");
            }

            @Override
            public Result<PageResult<RecycleBinLinkPageResponseDto>> pageShortLinkRecycleBin(ShortLinkRecycleBinPageQueryDTO requestParam) {
                log.warn("[回收站降级] 分页查询不可用，返回空数据");
                return Result.success(new PageResult<>());
            }

            @Override
            public Result recoverRecycleBin(RecycleBinRecoverRequestDto recoverRequest) {
                log.warn("[回收站降级] 恢复链接失败，触发异步补偿，参数：{}", recoverRequest);
                rabbitTemplate.convertAndSend(EXCHANGE, RECOVER_ROUTING_KEY, recoverRequest);
                return Result.success("恢复请求已提交，系统处理中...");
            }

            @Override
            public Result removeRecycleBin(RecycleBinRemoveRequestDto removeRequest) {
                log.warn("[回收站降级] 删除链接失败，触发异步补偿，参数：{}", removeRequest);
                rabbitTemplate.convertAndSend(EXCHANGE, REMOVE_ROUTING_KEY, removeRequest);
                return Result.success("删除请求已提交，系统处理中...");
            }
        };
    }
}
