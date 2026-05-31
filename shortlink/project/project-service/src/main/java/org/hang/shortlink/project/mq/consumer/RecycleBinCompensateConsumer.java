package org.hang.shortlink.project.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.service.IRecycleBinService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecycleBinCompensateConsumer {

    private static final String SAVE_QUEUE = "short.link.recycle.save.queue";
    private static final String RECOVER_QUEUE = "short.link.recycle.recover.queue";
    private static final String REMOVE_QUEUE = "short.link.recycle.remove.queue";

    private final IRecycleBinService recycleBinService;

    @RabbitListener(queues = SAVE_QUEUE)
    public void consumeSave(RecycleBinSaveRequestDto request, Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            recycleBinService.saveRecycleBin(request);
            channel.basicAck(tag, false);
            log.info("[回收站补偿] 保存成功：{}", request);
        } catch (Exception e) {
            log.error("[回收站补偿] 保存失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[回收站补偿] 保存死信投递失败", ex);
            }
        }
    }

    @RabbitListener(queues = RECOVER_QUEUE)
    public void consumeRecover(RecycleBinRecoverRequestDto request, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            recycleBinService.recoverRecycleBin(request);
            channel.basicAck(tag, false);
            log.info("[回收站补偿] 恢复成功：{}", request);
        } catch (Exception e) {
            log.error("[回收站补偿] 恢复失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[回收站补偿] 恢复死信投递失败", ex);
            }
        }
    }

    @RabbitListener(queues = REMOVE_QUEUE)
    public void consumeRemove(RecycleBinRemoveRequestDto request, Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            recycleBinService.removeRecycleBin(request);
            channel.basicAck(tag, false);
            log.info("[回收站补偿] 删除成功：{}", request);
        } catch (Exception e) {
            log.error("[回收站补偿] 删除失败", e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[回收站补偿] 删除死信投递失败", ex);
            }
        }
    }
}
