package org.hang.shortlink.project.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@TableName("t_link_network_stats")
public class LinkNetworkStats {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String fullShortUrl;
    private Integer cnt;
    private String network;
    private LocalDate date;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @Builder.Default
    private Boolean delFlag = false;
}