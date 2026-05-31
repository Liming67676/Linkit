package org.hang.shortlink.project.api.config;

import org.hang.shortlink.project.api.client.fallback.RecycleBinClientFallBack;
import org.hang.shortlink.project.api.client.fallback.ShortLinkClientFallBack;
import org.hang.shortlink.project.api.client.fallback.ShortLinkStatsClientFallBack;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallBackConfig {

    @Bean
    public ShortLinkClientFallBack shortLinkClientFallBack(RabbitTemplate rabbitTemplate){
        return new ShortLinkClientFallBack(rabbitTemplate);
    }

    @Bean
    public RecycleBinClientFallBack recycleBinClientFallBack(RabbitTemplate rabbitTemplate){
        return new RecycleBinClientFallBack(rabbitTemplate);
    }

    @Bean
    public ShortLinkStatsClientFallBack shortLinkStatsClientFallBack(){
        return new ShortLinkStatsClientFallBack();
    }
}
