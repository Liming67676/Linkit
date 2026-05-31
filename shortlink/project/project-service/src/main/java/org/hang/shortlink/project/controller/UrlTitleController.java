package org.hang.shortlink.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.service.ILinkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL标题获取", description = "根据URL获取网站标题相关接口")
public class UrlTitleController {

    private final ILinkService linkService;

    /**
     * 根据 URL 获取对应网站的标题
     */
    @GetMapping("/api/short-link/v1/title")
    @Operation(summary = "根据URL获取标题", description = "根据指定的URL获取对应网站的标题")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return Result.success(linkService.getTitleByUrl(url));
    }
}
