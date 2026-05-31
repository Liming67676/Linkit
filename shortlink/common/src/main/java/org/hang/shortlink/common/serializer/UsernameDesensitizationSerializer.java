package org.hang.shortlink.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author zhaohang1216
 * @createTime 2026--05
 * @description 用户名脱敏
 */
public class UsernameDesensitizationSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String username, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(username(username));
    }

    private  String username(String username) {
        if (username == null || username.isEmpty()) {
            return username;
        }
        // 截取第一个字符
        String firstChar = username.substring(0, 1);
        // 计算剩余字符长度
        int restLength = username.length() - 1;
        // 拼接星号
        return firstChar + getAsterisk(restLength);
    }
    /**
     * 生成指定数量的星号
     */
    private  String getAsterisk(int length) {
        if (length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("*");
        }
        return sb.toString();
    }
}
