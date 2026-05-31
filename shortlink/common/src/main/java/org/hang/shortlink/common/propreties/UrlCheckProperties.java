package org.hang.shortlink.common.propreties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "short.link.url-check")
public class UrlCheckProperties {

    private boolean enabled;

    private boolean whitelistEnabled;

    private List<String> blacklistDomains;

    private List<String> whitelistDomains;

    private Integer cacheSeconds;

    private TencentCms tencentCms;

    @Data
    public static class TencentCms {
        private String secretId;
        private String secretKey;
        private String bizType;
    }
}
