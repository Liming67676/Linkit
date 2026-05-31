package org.hang.shortlink.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.service.RecycleBinService;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站管理控制层
 */
@RestController(value = "recycleBinControllerByAdmin")
@RequiredArgsConstructor
@Tag(name = "回收站管理(后台)", description = "短链接回收站相关操作接口")
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 保存回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    @Operation(summary = "保存短链接到回收站", description = "将指定短链接移入回收站")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveRequestDto requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Result.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/short-link/admin/v1/recycle-bin/page")
    @Operation(summary = "分页查询回收站短链接", description = "分页获取回收站中的短链接列表")
    public Result<PageResult<RecycleBinLinkPageResponseDto>> pageShortLink(ShortLinkRecycleBinPageQueryDTO requestParam) {
        return recycleBinService.pageRecycleBinShortLink(requestParam);
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    @Operation(summary = "恢复短链接", description = "从回收站中恢复指定短链接")
    public Result<Void> recoverRecycleBin(@RequestBody RecycleBinRecoverRequestDto requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Result.success();
    }

    /**
     * 移除短链接
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/remove")
    @Operation(summary = "移除短链接", description = "从回收站中永久删除指定短链接")
    public Result<Void> removeRecycleBin(@RequestBody RecycleBinRemoveRequestDto requestParam) {
        recycleBinService.removeRecycleBin(requestParam);
        return Result.success();
    }
}