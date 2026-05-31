package org.hang.shortlink.project.mq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.mq.dto.FaviconMessage;
import org.hang.shortlink.project.pojo.po.Link;
import org.hang.shortlink.project.service.ILinkService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import static org.hang.shortlink.project.mq.config.RabbitMQConfig.FAVICON_QUEUE;

/**
 * 处理favicon更新的MQ消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FaviconConsumer {

    private final ILinkService linkService;

    @RabbitListener(queues = FAVICON_QUEUE)
    public void consumeFavicon(FaviconMessage message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            String faviconUrl = linkService.getFaviconUrl(message.getOriginUrl());
            if (faviconUrl == null) {
                channel.basicAck(tag, false);
                return;
            }


            boolean updated = linkService.lambdaUpdate()
                    .eq(Link::getFullShortUrl, message.getFullShortUrl())
                    .eq(Link::getGid, message.getGid())
                    .set(Link::getFavicon, faviconUrl)
                    .update();
            if (updated) {
                log.debug("[MQ消费者] favicon更新成功: {} -> {}", message.getFullShortUrl(), faviconUrl);
            }
            // 正常消费 → 手动确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[MQ消费者] favicon处理失败: {}", message.getFullShortUrl(), e);
            try {
                // 参数说明：
                // 消息标识,
                // 是否批量拒绝, 
                // 是否重回队列
                // 消费失败 → 拒绝消息，进入死信队列
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[MQ消费者] favicon死信投递失败", ex);
            }
        }
    }
}
