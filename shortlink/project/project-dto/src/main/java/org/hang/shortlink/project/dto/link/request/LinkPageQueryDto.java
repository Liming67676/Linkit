package org.hang.shortlink.project.dto.link.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接分页查询参数", description = "分页查询短链接的请求参数")
public class LinkPageQueryDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 分组id
     */
    @Schema(description = "分组id")
    private String gid;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码，默认1")
    private Long current;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数，默认10")
    private Long size;

    @Schema(description = "排序标识")
    private String orderTag;

    @Schema(description = "排序方向，asc/desc")
    private String orderType;

}
