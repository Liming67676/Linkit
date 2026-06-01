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
                log.warn("[短链接降级] 创建短链接不可用，参数：{}", createRequest);
                return Result.failure("短链接服务暂时不可用，请稍后重试");
            }

            @Override
            public Result batchCreateShortLink(ShortLinkBatchCreateRequestDto batchCreateRequest) {
                log.warn("[短链接降级] 批量创建短链接不可用，参数：{}", batchCreateRequest);
                return Result.failure("短链接服务暂时不可用，请稍后重试");
            }

            @Override
            public Result updateLink(LinkUpdateRequestDto updateRequest) {
                log.warn("[短链接降级] 修改短链接不可用，参数：{}", updateRequest);
                return Result.failure("短链接服务暂时不可用，请稍后重试");
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
