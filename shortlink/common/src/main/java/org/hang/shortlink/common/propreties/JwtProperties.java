package org.hang.shortlink.common.propreties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "short.link.jwt")
public class JwtProperties {

    /**
     * 工生成jwt令牌相关配置
     */
    private String SecretKey; // 密钥
    private Long ttl; // 有效时间
    private String tokenName; // token名

}