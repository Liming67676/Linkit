package org.hang.shortlink.project.dto.link.reseponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hang.shortlink.project.dto.link.LinkBaseInfo;

import java.io.Serializable;
import java.util.List;

/**
 *  批量创建成功返回信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接批量创建响应", description = "批量创建短链接成功后返回的信息")
public class LinkBatchCreateResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功数量
     */
    @Schema(description = "成功创建数量")
    private Integer total;

    /**
     * 批量创建返回参数
     */
    @Schema(description = "批量创建的短链接基本信息列表")
    private List<LinkBaseInfo> baseLinkInfos;
}