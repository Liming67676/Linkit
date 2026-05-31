package org.hang.shortlink.common.propreties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "short.link.rate-limit")
public class RateLimitProperties {
    private boolean enabled = true;
    private int accessQps = 300;
    private int createQps = 30;
    private List<String> includePaths;
    private List<String> createPaths;
}