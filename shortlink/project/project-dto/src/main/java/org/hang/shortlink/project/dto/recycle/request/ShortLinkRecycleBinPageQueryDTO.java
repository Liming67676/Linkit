package org.hang.shortlink.project.dto.recycle.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "回收站分页查询参数", description = "分页查询回收站短链接的请求参数")
public class ShortLinkRecycleBinPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long current;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "分组标识列表")
    private List<String> gidList;
}
