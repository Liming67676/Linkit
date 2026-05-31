package org.hang.shortlink.project.dto.link;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  返回的短链接基本信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接基本信息", description = "短链接基本信息响应")
public class LinkBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 描述信息
     */
    @Schema(description = "描述信息")
    private String describe;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;
}