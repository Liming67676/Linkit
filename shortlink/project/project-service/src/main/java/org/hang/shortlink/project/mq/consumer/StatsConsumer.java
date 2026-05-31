package org.hang.shortlink.project.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.mq.dto.StatsMessage;
import org.hang.shortlink.project.service.*;
import org.hang.shortlink.project.utils.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.hang.shortlink.project.mq.config.RabbitMQConfig.STATS_QUEUE;

/**
 * 统计信息处理的MQ消费者
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsConsumer {

    private final ILinkLocaleStatsService linkLocaleStatsService;
    private final ILinkOsStatsService linkOsStatsService;
    private final ILinkBrowserStatsService linkBrowserStatsService;
    private final ILinkDeviceStatsService linkDeviceStatsService;
    private final ILinkNetworkStatsService linkNetworkStatsService;
    private final ILinkAccessLogStatsService linkAccessLogStatsService;

    @RabbitListener(queues = STATS_QUEUE)
    public void consumeStats(StatsMessage message, Channel channel,
                             @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            String fullShortUrl = message.getFullShortUrl();

            linkLocaleStatsService.updateLinkLocaleStats(fullShortUrl, message.getIp());
            linkOsStatsService.updateLinkOsStats(fullShortUrl, message.getOsName());
            linkBrowserStatsService.updateLinkBrowserStats(fullShortUrl, message.getBrowserName());
            linkDeviceStatsService.updateLinkDeviceStats(fullShortUrl, message.getDeviceName());
            linkNetworkStatsService.updateLinkNetworkStats(fullShortUrl, message.getNetworkType());

            linkAccessLogStatsService.insertAccessLog(fullShortUrl, message.getIp(),
                    message.getOsName(), message.getBrowserName(),
                    message.getDeviceName(), message.getNetworkType());

            log.debug("[MQ消费者] 统计处理完成: {}", fullShortUrl);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[MQ消费者] 统计处理失败: {}", message.getFullShortUrl(), e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[MQ消费者] 统计处理失败: {}", message.getFullShortUrl(), ex);
            }
        }
    }
}
