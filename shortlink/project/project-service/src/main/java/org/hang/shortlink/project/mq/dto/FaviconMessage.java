package org.hang.shortlink.project.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图标消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaviconMessage implements Serializable {
    private String fullShortUrl;
    private String originUrl;
    private String gid;
}
