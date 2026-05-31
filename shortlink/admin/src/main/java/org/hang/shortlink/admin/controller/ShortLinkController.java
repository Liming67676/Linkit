package org.hang.shortlink.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hang.shortlink.admin.service.ShortLinkActualRemoteService;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接后管控制层
 */
@RestController(value = "shortLinkControllerByAdmin")
@RequiredArgsConstructor
@Tag(name = "短链接管理(后台)", description = "短链接增删改查相关操作接口")
public class ShortLinkController {

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    @Operation(summary = "创建短链接", description = "创建一个新的短链接")
    public Result<LikCreateResponseDto> createShortLink(@RequestBody LinkCreateRequestDto createRequest) {
        return shortLinkActualRemoteService.createShortLink(createRequest);
    }

    /**
     * 批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/create/batch")
    @Operation(summary = "批量创建短链接", description = "批量创建多个短链接")
    public Result<Void> batchCreateShortLink(@Validated @RequestBody ShortLinkBatchCreateRequestDto batchCreateRequest, HttpServletResponse response) {
        Result<LinkBatchCreateResponseDto> linkList = shortLinkActualRemoteService.batchCreateShortLink(batchCreateRequest);
        return Result.success();
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/admin/v1/update")
    @Operation(summary = "修改短链接", description = "更新指定短链接的信息")
    public Result<Void> updateShortLink(@RequestBody LinkUpdateRequestDto updateRequest) {
        shortLinkActualRemoteService.updateShortLink(updateRequest);
        return Result.success();
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    @Operation(summary = "分页查询短链接", description = "分页获取短链接列表")
    public Result<PageResult<LinkPageResponseDto>> pageShortLink(LinkPageQueryDto pageQuery) {
        return shortLinkActualRemoteService.pageShortLink(pageQuery);
    }


    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/admin/v1/count")
    @Operation(summary = "查询短链接分组内数量", description = "查询短链接分组内数量")
    public Result<List<LinkGroupCountResponseDto>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam){
        return shortLinkActualRemoteService.listGroupShortLinkCount(requestParam);
    }

}
