package org.hang.shortlink.project.api.client;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.fallback.ShortLinkClientFallBack;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description
 */
@FeignClient(value = "shortlink-project-service",
        contextId = "shortLinkClient", // 唯一标识
         fallbackFactory = ShortLinkClientFallBack.class)
public interface ShortLinkClient {
    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    Result<PageResult<LinkPageResponseDto>> pageLink(@SpringQueryMap LinkPageQueryDto pageQuery);


    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    Result<LikCreateResponseDto> createLink(@RequestBody LinkCreateRequestDto createRequest);

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/create/batch")
    Result<LinkBatchCreateResponseDto> batchCreateShortLink(@RequestBody ShortLinkBatchCreateRequestDto batchCreateRequest);

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/v1/update")
    Result<Boolean> updateLink(@RequestBody LinkUpdateRequestDto updateRequest);

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/count")
    Result<List<LinkGroupCountResponseDto>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 根据 URL 获取对应网站的标题
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

}
