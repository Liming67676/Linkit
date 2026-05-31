package org.hang.shortlink.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.service.ShortLinkActualRemoteService;
import org.hang.shortlink.common.pojo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL 标题控制层
 */
@RestController(value = "urlTitleControllerByAdmin")
@RequiredArgsConstructor
@Tag(name = "URL标题获取(后台)", description = "根据URL获取网站标题相关接口")
public class UrlTitleController {

    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    /**
     * 根据URL获取对应网站的标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    @Operation(summary = "根据URL获取标题", description = "根据指定的URL获取对应网站的标题")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return shortLinkActualRemoteService.getTitleByUrl(url);
    }
}