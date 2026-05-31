package org.hang.shortlink.project.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import org.hang.shortlink.project.service.ILinkService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-21
 */
@RestController
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "短链接管理", description = "短链接增删改查及跳转相关接口")
public class LinkController {

    private final ILinkService linkService;


    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/v1/page")
    @Operation(summary = "分页查询短链接", description = "分页获取短链接列表")
    public Result<PageResult<LinkPageResponseDto>> pageLink(LinkPageQueryDto pageQuery){
        PageResult<LinkPageResponseDto> pageDTO = linkService.pageLink(pageQuery);
        return Result.success(pageDTO);
    }


    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    @Operation(summary = "创建短链接", description = "创建一个新的短链接")
    public Result<LikCreateResponseDto> createLink(@RequestBody LinkCreateRequestDto createRequest){
        LikCreateResponseDto createVO = linkService.createLink(createRequest);
        return Result.success(createVO);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/create/batch")
    @Operation(summary = "批量创建短链接", description = "批量创建多个短链接")
    public Result<LinkBatchCreateResponseDto> batchCreateShortLink(@RequestBody ShortLinkBatchCreateRequestDto batchCreateRequest) {
        return Result.success(linkService.batchCreateShortLink(batchCreateRequest));
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/v1/update")
    @Operation(summary = "修改短链接", description = "更新指定短链接的信息")
    public Result<Boolean> updateLink(@RequestBody LinkUpdateRequestDto updateRequest){
        Boolean flag = linkService.updateLink(updateRequest);
        return Result.success(Boolean.TRUE.equals(flag));
    }

    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/count")
    @Operation(summary = "查询分组短链接数量", description = "查询指定分组内的短链接数量")
    public Result<List<LinkGroupCountResponseDto>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam) {
        return Result.success(linkService.listGroupShortLinkCount(requestParam));
    }


    /**
     * 短链接跳转原始链接
     */
    @GetMapping("/{short-uri:[a-zA-Z0-9]{6,8}}")
    @Operation(summary = "短链接跳转", description = "短链接跳转到原始URL")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {

        linkService.restoreUrl(shortUri, request, response);
    }
}
