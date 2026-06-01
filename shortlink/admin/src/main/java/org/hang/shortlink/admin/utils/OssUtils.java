package org.hang.shortlink.admin.utils;

import cn.hutool.crypto.SecureUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.propreties.OssProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OssUtils {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    /**
     * 上传二维码图片到OSS
     */
    public String uploadQrCode(String content, int width, int height, byte[] qrBytes) {
        String md5Str = SecureUtil.md5(content);
        String objectKey = "qr/" + md5Str + "_" + width + "x" + height + ".png";
        String bucketName = ossProperties.getBucket();

        boolean exists = ossClient.doesObjectExist(bucketName, objectKey);
        if (exists) {
            log.info("二维码已存在，返回后端预览链接：{}", objectKey);
            return buildPreviewUrl(objectKey);
        }

        // 上传逻辑不变
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        metadata.setContentDisposition("inline");
        metadata.setContentLength(qrBytes.length);
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey, new ByteArrayInputStream(qrBytes), metadata);
        ossClient.putObject(putRequest);
        log.info("二维码上传完成，key={}", objectKey);

        return buildPreviewUrl(objectKey);
    }

    /**
     * 通过后端代理输出图片，避免 OSS 默认域名触发强制下载。
     */
    public void previewQrCode(String objectKey, HttpServletResponse response) {
        if (!objectKey.startsWith("qr/") || !objectKey.endsWith(".png")) {
            throw new ServiceException("二维码地址非法");
        }
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "inline; filename=\"qrcode.png\"");
        response.setHeader("Cache-Control", "private, max-age=300");
        try (OSSObject ossObject = ossClient.getObject(ossProperties.getBucket(), objectKey)) {
            StreamUtils.copy(ossObject.getObjectContent(), response.getOutputStream());
        } catch (Exception ex) {
            log.error("二维码预览失败，key={}", objectKey, ex);
            throw new ServiceException("二维码预览失败");
        }
    }

    private String buildPreviewUrl(String objectKey) {
        return "/api/short-link/admin/v1/QR/preview?objectKey="
                + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
    }
}
