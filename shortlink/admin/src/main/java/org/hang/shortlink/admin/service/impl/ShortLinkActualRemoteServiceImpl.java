package org.hang.shortlink.admin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.admin.service.ShortLinkActualRemoteService;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.project.api.client.ShortLinkClient;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShortLinkActualRemoteServiceImpl implements ShortLinkActualRemoteService {

    private static final long SLOW_CREATE_LINK_REMOTE_MILLIS = 800L;

    private final ShortLinkClient shortLinkClient;
    @Override
    public Result<LikCreateResponseDto> createShortLink(LinkCreateRequestDto createRequest) {
        long startTime = System.currentTimeMillis();
        Result<LikCreateResponseDto> result = shortLinkClient.createLink(createRequest);
        long costMs = System.currentTimeMillis() - startTime;
        if (costMs >= SLOW_CREATE_LINK_REMOTE_MILLIS) {
            log.info("admin create short link remote slow originUrl={}, gid={}, costMs={}",
                    createRequest.getOriginUrl(), createRequest.getGid(), costMs);
        }
        return result;
    }

    @Override
    public Result<LinkBatchCreateResponseDto> batchCreateShortLink(ShortLinkBatchCreateRequestDto batchCreateRequest) {
        return shortLinkClient.batchCreateShortLink(batchCreateRequest);
    }

    @Override
    public void updateShortLink(LinkUpdateRequestDto updateRequest) {
        shortLinkClient.updateLink(updateRequest);
    }

    @Override
    public Result<PageResult<LinkPageResponseDto>> pageShortLink(LinkPageQueryDto pageQuery) {
        return shortLinkClient.pageLink(pageQuery);
    }

    @Override
    public Result<String> getTitleByUrl(String url) {
        return shortLinkClient.getTitleByUrl(url);
    }

    @Override
    public Result<List<LinkGroupCountResponseDto>> listGroupShortLinkCount(List<String> requestParam) {
        return shortLinkClient.listGroupShortLinkCount(requestParam);
    }
}
