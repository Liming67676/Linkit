package org.hang.shortlink.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.admin.utils.OssUtils;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.utils.QrCodeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "二维码生成", description = "短链接二维码生成相关接口")
public class QrCodeController {

    private final OssUtils ossUtils;

    @GetMapping("/api/short-link/admin/v1/QR")
    @Operation(summary = "生成短链接二维码", description = "根据短链接地址生成二维码并上传至OSS，返回可访问的图片URL")
    public Result<Map<String, String>> generateQrCode(
            @RequestParam("url") String url,
            @RequestParam(value = "width", defaultValue = "300") int width,
            @RequestParam(value = "height", defaultValue = "300") int height) {

        if (width < 100 || width > 1000 || height < 100 || height > 1000) {
            throw new RuntimeException("二维码尺寸非法，范围：100-1000");
        }

        byte[] qrBytes = QrCodeUtils.generateQrCodeBytes(url, width, height);
        String ossUrl = ossUtils.uploadQrCode(url, width, height, qrBytes);

        return Result.success(Map.of("url", ossUrl));
    }
}
