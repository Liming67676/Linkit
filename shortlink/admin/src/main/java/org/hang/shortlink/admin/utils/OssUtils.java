package org.hang.shortlink.admin.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.propreties.OssProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

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

        // 先判断文件是否已存在
        boolean exists = ossClient.doesObjectExist(bucketName, objectKey);
        if (exists) {
            log.info("二维码已存在于OSS，直接返回：{}", objectKey);
            // 直接返回已存在的URL，不上传
            return "https://" + bucketName + "." + ossProperties.getEndpoint() + "/" + objectKey;
        }

        // 不存在 → 执行上传
        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                objectKey,
                new ByteArrayInputStream(qrBytes)
        );
        ossClient.putObject(request);
        log.info("二维码已上传至OSS，key={}", objectKey);

        return "https://" + bucketName + "." + ossProperties.getEndpoint() + "/" + objectKey;
    }
}
