package org.hang.shortlink.project.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.service.ILinkService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkCompensateConsumer {

    private static final String CREATE_QUEUE = "short.link.create.queue";
    private static final String BATCH_CREATE_QUEUE = "short.link.batch.create.queue";
    private static final String UPDATE_QUEUE = "short.link.update.queue";

    private final ILinkService linkService;

    @RabbitListener(queues = CREATE_QUEUE)
    public void consumeCreate(LinkCreateRequestDto request, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            linkService.createLink(request);
            channel.basicAck(tag, false);
            log.info("[短链接补偿] 创建成功：{}", request);
        } catch (Exception e) {
            log.error("[短链接补偿] 创建失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[短链接补偿] 创建死信投递失败", ex);
            }
        }
    }

    @RabbitListener(queues = BATCH_CREATE_QUEUE)
    public void consumeBatchCreate(ShortLinkBatchCreateRequestDto request, Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            linkService.batchCreateShortLink(request);
            channel.basicAck(tag, false);
            log.info("[短链接补偿] 批量创建成功：{}", request);
        } catch (Exception e) {
            log.error("[短链接补偿] 批量创建失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[短链接补偿] 批量创建死信投递失败", ex);
            }
        }
    }

    @RabbitListener(queues = UPDATE_QUEUE)
    public void consumeUpdate(LinkUpdateRequestDto request, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            linkService.updateLink(request);
            channel.basicAck(tag, false);
            log.info("[短链接补偿] 修改成功：{}", request);
        } catch (Exception e) {
            log.error("[短链接补偿] 修改失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[短链接补偿] 修改死信投递失败", ex);
            }
        }
    }
}
