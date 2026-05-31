package org.hang.shortlink.project.api.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.ShortLinkClient;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链接服务降级处理
 */
@Slf4j
public class ShortLinkClientFallBack implements FallbackFactory<ShortLinkClient> {

    private static final String EXCHANGE = "short.link.direct";
    private static final String CREATE_ROUTING_KEY = "short.link.create";
    private static final String BATCH_CREATE_ROUTING_KEY = "short.link.batch.create";
    private static final String UPDATE_ROUTING_KEY = "short.link.update";

    private final RabbitTemplate rabbitTemplate;

    public ShortLinkClientFallBack(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ShortLinkClient create(Throwable cause) {
        log.error("[短链接服务降级] 服务不可用，异常信息：", cause);
        return new ShortLinkClient() {

            @Override
            public Result<PageResult<LinkPageResponseDto>> pageLink(LinkPageQueryDto pageQuery) {
                log.warn("[短链接降级] 分页查询不可用，返回空数据");
                return Result.success(new PageResult<>());
            }

            @Override
            public Result createLink(LinkCreateRequestDto createRequest) {
                log.warn("[短链接降级] 创建短链接失败，触发异步补偿，参数：{}", createRequest);
                rabbitTemplate.convertAndSend(EXCHANGE, CREATE_ROUTING_KEY, createRequest);
                return Result.success("创建请求已提交，系统处理中...");
            }

            @Override
            public Result batchCreateShortLink(ShortLinkBatchCreateRequestDto batchCreateRequest) {
                log.warn("[短链接降级] 批量创建短链接失败，触发异步补偿，参数：{}", batchCreateRequest);
                rabbitTemplate.convertAndSend(EXCHANGE, BATCH_CREATE_ROUTING_KEY, batchCreateRequest);
                return Result.success("批量创建请求已提交，系统处理中...");
            }

            @Override
            public Result updateLink(LinkUpdateRequestDto updateRequest) {
                log.warn("[短链接降级] 修改短链接失败，触发异步补偿，参数：{}", updateRequest);
                rabbitTemplate.convertAndSend(EXCHANGE, UPDATE_ROUTING_KEY, updateRequest);
                return Result.success("修改请求已提交，系统处理中...");
            }

            @Override
            public Result listGroupShortLinkCount(List<String> requestParam) {
                log.warn("[短链接降级] 分组数量查询不可用，返回空列表");
                return Result.success(Collections.emptyList());
            }

            @Override
            public Result<String> getTitleByUrl(String url) {
                log.warn("[短链接降级] 获取标题不可用，返回空");
                return Result.success("");
            }
        };
    }
}
