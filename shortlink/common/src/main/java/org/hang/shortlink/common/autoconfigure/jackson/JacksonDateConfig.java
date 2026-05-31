package org.hang.shortlink.common.autoconfigure.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 日期序列化配置
 */
@Configuration
public class JacksonDateConfig {
    // 统一日期格式
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    // 参数注入官方Builder，不自己new
    @Bean
    public SimpleModule customJacksonModule() {
        SimpleModule module = new SimpleModule();
        // 全局注册序列化/反序列化器
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        return module;
    }
}