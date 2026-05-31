package org.hang.shortlink.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * shortlink-project-service服务属性热更新配置
 */
@Data
@ConfigurationProperties(prefix = "short.link")
public class ShortLinkProperties {
    /**
     * 最大分组数目
     */
    private Integer maxGroup;
}