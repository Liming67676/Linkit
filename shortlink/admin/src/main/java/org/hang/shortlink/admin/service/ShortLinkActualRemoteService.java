package org.hang.shortlink.admin.service;

import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;

import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface ShortLinkActualRemoteService {
    /**
     * 创建短链接
     * @param createRequest
     * @return
     */
    Result<LikCreateResponseDto> createShortLink(LinkCreateRequestDto createRequest);

    /**
     * 批量创建短链接
     * @param batchCreateRequest
     * @return
     */
    Result<LinkBatchCreateResponseDto> batchCreateShortLink(ShortLinkBatchCreateRequestDto batchCreateRequest);

    /**
     * 更新短链接
     * @param updateRequest
     */
    void updateShortLink(LinkUpdateRequestDto updateRequest);

    /**
     * 分页查询短链接
     * @param pageQuery
     * @return
     */
    Result<PageResult<LinkPageResponseDto>> pageShortLink(LinkPageQueryDto pageQuery);

    /**
     * 根据URL获取对应网站的标题
     * @param url 原链接url
     * @return
     */
    Result<String> getTitleByUrl(String url);

    Result<List<LinkGroupCountResponseDto>> listGroupShortLinkCount(List<String> requestParam);
}
