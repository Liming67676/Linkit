package org.hang.shortlink.admin.pojo.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "分组请求参数", description = "短链接分组请求参数")
public class GroupReqDto {


    @Schema(description = "分组标识")
    private String gid; // 分组标识
    
    @Schema(description = "分组名称")
    private String name;

    @Schema(description = "分组排序")
    private Integer sortOrder; // 分组排序
}
