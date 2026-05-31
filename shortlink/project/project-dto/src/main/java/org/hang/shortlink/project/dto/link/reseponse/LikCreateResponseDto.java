package org.hang.shortlink.project.dto.link.reseponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接创建响应", description = "创建短链接成功后返回的信息")
public class LikCreateResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 完整短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;
}
