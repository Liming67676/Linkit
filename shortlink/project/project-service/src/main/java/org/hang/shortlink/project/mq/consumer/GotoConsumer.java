package org.hang.shortlink.project.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.mq.dto.LinkGotoMessage;
import org.hang.shortlink.project.pojo.po.LinkGoto;
import org.hang.shortlink.project.service.ILinkGotoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.hang.shortlink.project.mq.config.RabbitMQConfig.GOTO_QUEUE;

/**
 * 处理LinkGoto的MQ消费者
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GotoConsumer {

    private final ILinkGotoService linkGotoService;

    @RabbitListener(queues = GOTO_QUEUE)
    public void consumeGoto(LinkGotoMessage message,Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            LinkGoto linkGoto = LinkGoto.builder()
                    .fullShortUrl(message.getFullShortUrl())
                    .gid(message.getGid())
                    .build();
            linkGotoService.save(linkGoto);
            log.debug("[MQ消费者] LinkGoto写入成功: {} -> {}", message.getFullShortUrl(), message.getGid());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[MQ消费者] LinkGoto写入失败: {}", message.getFullShortUrl(), e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[MQ消费者] LinkGoto写入失败: {}", message.getFullShortUrl(), ex);
            }
        }
    }
}
