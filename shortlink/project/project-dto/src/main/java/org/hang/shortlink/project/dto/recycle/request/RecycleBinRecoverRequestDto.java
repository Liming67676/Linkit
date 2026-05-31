package org.hang.shortlink.project.dto.recycle.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "回收站恢复请求参数", description = "从回收站恢复短链接的请求参数")
public class RecycleBinRecoverRequestDto implements Serializable {
    private final static long serialVersionUID = 1L;
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 全部短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;
}