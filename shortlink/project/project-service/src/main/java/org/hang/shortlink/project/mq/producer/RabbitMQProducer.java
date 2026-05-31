package org.hang.shortlink.project.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.mq.dto.FaviconMessage;
import org.hang.shortlink.project.mq.dto.LinkGotoMessage;
import org.hang.shortlink.project.mq.dto.StatsMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static org.hang.shortlink.project.mq.config.RabbitMQConfig.*;

/**
 * RabbitMQ生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送统计消息
     */
    public void sendStatsMessage(StatsMessage message) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, STATS_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("[MQ生产者] 发送统计消息失败: {}", message.getFullShortUrl(), e);
        }
    }

    /**
     * 发送favicon消息
     */
    public void sendFaviconMessage(FaviconMessage message) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, FAVICON_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("[MQ生产者] 发送favicon消息失败: {}", message.getFullShortUrl(), e);
        }
    }

    /**
     * 发送LinkGoto消息
     */
    public void sendGotoMessage(LinkGotoMessage message) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, GOTO_ROUTING_KEY, message);
        } catch (Exception e) {
            log.error("[MQ生产者] 发送LinkGoto消息失败: {}", message.getFullShortUrl(), e);
        }
    }
}
