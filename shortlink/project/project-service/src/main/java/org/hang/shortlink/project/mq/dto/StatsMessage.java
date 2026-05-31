package org.hang.shortlink.project.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统计消息DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsMessage implements Serializable {
    private String fullShortUrl;
    private String ip;
    private String osName;
    private String networkType;
    private String browserName;
    private String deviceName;
}
