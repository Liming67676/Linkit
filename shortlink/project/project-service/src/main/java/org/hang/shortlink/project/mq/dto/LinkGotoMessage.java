package org.hang.shortlink.project.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 链接重定位消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkGotoMessage implements Serializable {
    private String fullShortUrl;
    private String gid;
}
