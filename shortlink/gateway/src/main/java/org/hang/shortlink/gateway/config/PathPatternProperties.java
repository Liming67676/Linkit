package org.hang.shortlink.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@ConfigurationProperties(prefix = "short.link.interceptor")
public class PathPatternProperties {
    private List<String> includePaths;
    private List<String> excludePath;
}
