package org.hang.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRecoverRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinRemoveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.RecycleBinSaveRequestDto;
import org.hang.shortlink.project.dto.recycle.request.ShortLinkRecycleBinPageQueryDTO;
import org.hang.shortlink.project.dto.recycle.reseponse.RecycleBinLinkPageResponseDto;
import org.hang.shortlink.project.pojo.po.Link;

public interface IRecycleBinService extends IService<Link> {
    /**
     * 短链接移入回收站
     *
     * @param requestParam
     *         gid 分组标识
     *         fullShortUrl 完整短链接
     */
    void saveRecycleBin(RecycleBinSaveRequestDto requestParam);

    /**
     * 分页查询回收站记录
     *
     * @param requestParam gidList 分组标识列表
     * @return 回收站记录分页结果
     */
    PageResult<RecycleBinLinkPageResponseDto> pageShortLinkInRecycleBin(ShortLinkRecycleBinPageQueryDTO requestParam);

    /**
     * 恢复回收站记录
     *
     * @param requestParam gid 分组标识 fullShortUrl 完整短链接
     */
    void recoverRecycleBin(RecycleBinRecoverRequestDto requestParam);

    /**
     * 从回收站中删除记录
     *
     * @param requestParam gid 分组标识 fullShortUrl 完整短链接
     */
    void removeRecycleBin(RecycleBinRemoveRequestDto requestParam);
}