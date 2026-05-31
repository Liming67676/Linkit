package org.hang.shortlink.common.propreties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "short.link.oss")
public class OssProperties {

    private String accessKeyId;
    private String accessKeySecret;
    private String endpoint;
    private String bucket;
    private String region;
}
