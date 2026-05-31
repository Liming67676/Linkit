package org.hang.shortlink.project.dto.link.reseponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "分组短链接数量响应", description = "分组内短链接数量统计")
public class LinkGroupCountResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分组标识")
    private String gid;

    @Schema(description = "短链接数量")
    private Integer shortLinkCount;
}