package org.hang.shortlink.project.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlSecurityMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String originUrl;

    private String fullShortUrl;

    private String gid;
}
