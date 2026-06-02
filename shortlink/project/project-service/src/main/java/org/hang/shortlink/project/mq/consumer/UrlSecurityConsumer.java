package org.hang.shortlink.project.mq.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.constant.RedisConstant;
import org.hang.shortlink.project.mq.dto.UrlSecurityMessage;
import org.hang.shortlink.project.pojo.po.Link;
import org.hang.shortlink.project.pojo.po.LinkGoto;
import org.hang.shortlink.project.service.ILinkGotoService;
import org.hang.shortlink.project.service.ILinkService;
import org.hang.shortlink.project.utils.UrlSecurityCheckHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hang.shortlink.project.mq.config.RabbitMQConfig.URL_SECURITY_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlSecurityConsumer {

    private final UrlSecurityCheckHandler urlSecurityCheckHandler;

    private final ILinkService linkService;

    private final ILinkGotoService linkGotoService;

    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = URL_SECURITY_QUEUE)
    public void consumeUrlSecurity(UrlSecurityMessage message, Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            boolean safe = urlSecurityCheckHandler.checkUrlByTencentTms(message.getOriginUrl());
            if (!safe) {
                disableUnsafeLink(message);
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("[MQ消费者] URL安全检测失败: {}", message.getFullShortUrl(), e);
            try {
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                log.error("[MQ消费者] URL安全检测死信投递失败", ex);
            }
        }
    }

    private void disableUnsafeLink(UrlSecurityMessage message) {
        boolean updated = linkService.lambdaUpdate()
                .set(Link::getEnableStatus, 1)
                .eq(Link::getGid, message.getGid())
                .eq(Link::getFullShortUrl, message.getFullShortUrl())
                .update();
        linkGotoService.remove(new LambdaQueryWrapper<LinkGoto>()
                .eq(LinkGoto::getGid, message.getGid())
                .eq(LinkGoto::getFullShortUrl, message.getFullShortUrl()));
        redisTemplate.delete(List.of(
                String.format(RedisConstant.FULL_SHORT_URL_KEY, message.getFullShortUrl()),
                String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, message.getGid())
        ));
        log.warn("[MQ消费者] URL安全检测不通过，已禁用短链接: {}, updated={}", message.getFullShortUrl(), updated);
    }
}
