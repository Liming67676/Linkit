package org.hang.shortlink.project.api.config;

import org.hang.shortlink.project.api.client.fallback.RecycleBinClientFallBack;
import org.hang.shortlink.project.api.client.fallback.ShortLinkClientFallBack;
import org.hang.shortlink.project.api.client.fallback.ShortLinkStatsClientFallBack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallBackConfig {

    @Bean
    public ShortLinkClientFallBack shortLinkClientFallBack(){
        return new ShortLinkClientFallBack();
    }

    @Bean
    public RecycleBinClientFallBack recycleBinClientFallBack(){
        return new RecycleBinClientFallBack();
    }

    @Bean
    public ShortLinkStatsClientFallBack shortLinkStatsClientFallBack(){
        return new ShortLinkStatsClientFallBack();
    }
}
