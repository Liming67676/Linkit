package org.hang.shortlink.common.autoconfigure.jwt;

import org.hang.shortlink.common.propreties.JwtProperties;
import org.hang.shortlink.common.utils.JwtUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnProperty(prefix = "short.link.jwt", name = "secret-key")
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }
}
