package org.hang.shortlink.admin.service;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface RecycleBinService {

    /**
     * 保存回收站
     * @param requestParam
     */
    void saveRecycleBin(RecycleBinSaveRequestDto requestParam);

    /**
     * 分页查询当前用户回收站短链接
     * @param requestParam 其中只用传递分页参数即可
     * @return
     */
    Result<PageResult<RecycleBinLinkPageResponseDto>> pageRecycleBinShortLink(ShortLinkRecycleBinPageQueryDTO requestParam);

    /**
     * 恢复回收站
     * @param requestParam
     */
    void recoverRecycleBin(RecycleBinRecoverRequestDto requestParam);

    /**
     * 移除回收站
     * @param requestParam
     */
    void removeRecycleBin(RecycleBinRemoveRequestDto requestParam);
}
