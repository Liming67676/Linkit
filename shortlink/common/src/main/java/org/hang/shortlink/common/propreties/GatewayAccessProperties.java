package org.hang.shortlink.common.propreties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "short.link.gateway-access")
public class GatewayAccessProperties {

    /**
     * 是否要求业务接口必须经过网关访问。
     */
    private Boolean enabled = false;

    /**
     * 网关注入的内部请求头名称。
     */
    private String headerName = "x-shortlink-gateway";

    /**
     * 网关注入的内部请求头值。
     */
    private String headerValue = "true";

    /**
     * 不需要网关内部请求头的路径。
     */
    private List<String> excludePath = List.of(
            "/page/**",
            "/page/**/*.html",
            "/*.html",
            "/*",
            "/doc.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**"
    );
}
