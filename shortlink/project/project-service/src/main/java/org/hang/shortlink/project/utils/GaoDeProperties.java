package org.hang.shortlink.project.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhaohang1216
 * @createTime 2026--04
     * @description 高德地图 IP定位服务配置
 */
@Data
@ConfigurationProperties(prefix = "short.link.gaode")
public class GaoDeProperties {
    private String accessKey;
    private String apiUrl = "https://restapi.amap.com/v3/ip?ip=%s&output=json&key=%s";
}
