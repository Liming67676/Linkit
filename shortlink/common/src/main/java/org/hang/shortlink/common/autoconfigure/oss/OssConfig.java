package org.hang.shortlink.common.autoconfigure.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.hang.shortlink.common.propreties.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "short.link.oss", name = "access-key-id")
public class OssConfig {

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(OssProperties properties) {
        return new OSSClientBuilder()
                .build(
                        properties.getEndpoint(),
                        properties.getAccessKeyId(),
                        properties.getAccessKeySecret()
                );
    }
}
