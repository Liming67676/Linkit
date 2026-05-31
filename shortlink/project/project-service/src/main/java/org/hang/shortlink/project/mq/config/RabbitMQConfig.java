package org.hang.shortlink.project.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类（短链接业务 + 完整死信队列）
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 配置JSON消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        // 自动生成消息ID，用于幂等去重、消息追踪（SaaS项目非常有用）
        converter.setCreateMessageIds(true);
        return converter;
    }

    // ==================== 核心业务常量 ====================
    public static final String EXCHANGE = "short.link.direct";
    public static final String STATS_QUEUE = "short.link.stats.queue";
    public static final String STATS_ROUTING_KEY = "short.link.stats";
    public static final String FAVICON_QUEUE = "short.link.favicon.queue";
    public static final String FAVICON_ROUTING_KEY = "short.link.favicon";
    public static final String GOTO_QUEUE = "short.link.goto.queue";
    public static final String GOTO_ROUTING_KEY = "short.link.goto";

    // ==================== 死信队列统一配置 ====================
    public static final String DLX_EXCHANGE = "short.link.direct.dlx";
    public static final String STATS_DLQ = "short.link.stats.dlq";
    public static final String FAVICON_DLQ = "short.link.favicon.dlq";
    public static final String GOTO_DLQ = "short.link.goto.dlq";

    // ==================== 交换机 ====================
    @Bean
    public DirectExchange shortLinkExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ==================== 业务队列（全部带死信） ====================
    @Bean
    public Queue statsQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", STATS_DLQ);
        return new Queue(STATS_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue faviconQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", FAVICON_DLQ);
        return new Queue(FAVICON_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue gotoQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", GOTO_DLQ);
        return new Queue(GOTO_QUEUE, true, false, false, args);
    }

    // ==================== 死信队列 ====================
    @Bean
    public Queue statsDlq() { return new Queue(STATS_DLQ, true); }
    @Bean
    public Queue faviconDlq() { return new Queue(FAVICON_DLQ, true); }
    @Bean
    public Queue gotoDlq() { return new Queue(GOTO_DLQ, true); }

    // ==================== 绑定 ====================
    @Bean public Binding statsBind() { return BindingBuilder.bind(statsQueue()).to(shortLinkExchange()).with(STATS_ROUTING_KEY); }
    @Bean public Binding faviconBind() { return BindingBuilder.bind(faviconQueue()).to(shortLinkExchange()).with(FAVICON_ROUTING_KEY); }
    @Bean public Binding gotoBind() { return BindingBuilder.bind(gotoQueue()).to(shortLinkExchange()).with(GOTO_ROUTING_KEY); }

    @Bean public Binding statsDlqBind() { return BindingBuilder.bind(statsDlq()).to(deadLetterExchange()).with(STATS_DLQ); }
    @Bean public Binding faviconDlqBind() { return BindingBuilder.bind(faviconDlq()).to(deadLetterExchange()).with(FAVICON_DLQ); }
    @Bean public Binding gotoDlqBind() { return BindingBuilder.bind(gotoDlq()).to(deadLetterExchange()).with(GOTO_DLQ); }

    // ==================== Feign降级补偿队列 ====================
    public static final String RECYCLE_BIN_SAVE_QUEUE = "short.link.recycle.save.queue";
    public static final String RECYCLE_BIN_RECOVER_QUEUE = "short.link.recycle.recover.queue";
    public static final String RECYCLE_BIN_REMOVE_QUEUE = "short.link.recycle.remove.queue";
    public static final String SHORT_LINK_CREATE_QUEUE = "short.link.create.queue";
    public static final String SHORT_LINK_BATCH_CREATE_QUEUE = "short.link.batch.create.queue";
    public static final String SHORT_LINK_UPDATE_QUEUE = "short.link.update.queue";

    public static final String RECYCLE_BIN_SAVE_ROUTING_KEY = "short.link.recycle.save";
    public static final String RECYCLE_BIN_RECOVER_ROUTING_KEY = "short.link.recycle.recover";
    public static final String RECYCLE_BIN_REMOVE_ROUTING_KEY = "short.link.recycle.remove";
    public static final String SHORT_LINK_CREATE_ROUTING_KEY = "short.link.create";
    public static final String SHORT_LINK_BATCH_CREATE_ROUTING_KEY = "short.link.batch.create";
    public static final String SHORT_LINK_UPDATE_ROUTING_KEY = "short.link.update";

    @Bean public Queue recycleSaveQueue() { return new Queue(RECYCLE_BIN_SAVE_QUEUE, true); }
    @Bean public Queue recycleRecoverQueue() { return new Queue(RECYCLE_BIN_RECOVER_QUEUE, true); }
    @Bean public Queue recycleRemoveQueue() { return new Queue(RECYCLE_BIN_REMOVE_QUEUE, true); }
    @Bean public Queue shortLinkCreateQueue() { return new Queue(SHORT_LINK_CREATE_QUEUE, true); }
    @Bean public Queue shortLinkBatchCreateQueue() { return new Queue(SHORT_LINK_BATCH_CREATE_QUEUE, true); }
    @Bean public Queue shortLinkUpdateQueue() { return new Queue(SHORT_LINK_UPDATE_QUEUE, true); }

    @Bean public Binding recycleSaveBind() { return BindingBuilder.bind(recycleSaveQueue()).to(shortLinkExchange()).with(RECYCLE_BIN_SAVE_ROUTING_KEY); }
    @Bean public Binding recycleRecoverBind() { return BindingBuilder.bind(recycleRecoverQueue()).to(shortLinkExchange()).with(RECYCLE_BIN_RECOVER_ROUTING_KEY); }
    @Bean public Binding recycleRemoveBind() { return BindingBuilder.bind(recycleRemoveQueue()).to(shortLinkExchange()).with(RECYCLE_BIN_REMOVE_ROUTING_KEY); }
    @Bean public Binding shortLinkCreateBind() { return BindingBuilder.bind(shortLinkCreateQueue()).to(shortLinkExchange()).with(SHORT_LINK_CREATE_ROUTING_KEY); }
    @Bean public Binding shortLinkBatchCreateBind() { return BindingBuilder.bind(shortLinkBatchCreateQueue()).to(shortLinkExchange()).with(SHORT_LINK_BATCH_CREATE_ROUTING_KEY); }
    @Bean public Binding shortLinkUpdateBind() { return BindingBuilder.bind(shortLinkUpdateQueue()).to(shortLinkExchange()).with(SHORT_LINK_UPDATE_ROUTING_KEY); }
}