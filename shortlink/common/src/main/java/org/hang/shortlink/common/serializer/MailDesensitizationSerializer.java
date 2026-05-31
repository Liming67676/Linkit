package org.hang.shortlink.common.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author zhaohang1216
 * @createTime 2026--05
 * @description 邮箱脱敏
 */
public class MailDesensitizationSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String mail, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DesensitizedUtil.email(mail));
    }
}
