package org.hang.shortlink.project.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * 短链接不存在跳转控制器
 */
@Hidden
@Controller
public class ShortLinkNotFoundController {

    /**
     * 短链接不存在跳转页面
     */
    @RequestMapping("/page/notfound")
    public void notFound(HttpServletResponse response) throws IOException {
        // 重定向到子目录下的404页面，避免被短链接接口匹配
        response.sendRedirect("/page/404.html");
    }
}