package org.hang.shortlink.project.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.hang.shortlink.project.pojo.po.Link;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-21
 */
public interface ILinkService extends IService<Link> {

    /**
     * 创建当个短链接
     */
    LikCreateResponseDto createLink(LinkCreateRequestDto dto);

    /**
     * 更新短链接
     */
    Boolean updateLink(LinkUpdateRequestDto dto);

    /**
     * 分页查询一组中的短链接
     */
    PageResult<LinkPageResponseDto> pageLink(LinkPageQueryDto pageQuery);

    /**
     * 查询短链接分组内数量
     */
    List<LinkGroupCountResponseDto> listGroupShortLinkCount(List<String> requestParam);

    /**
     * 批量创建短链接
     */
    LinkBatchCreateResponseDto batchCreateShortLink(ShortLinkBatchCreateRequestDto requestParam);

    /**
     * 短链接跳转原始链接
     */
    void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 添加点击数
     */
    void addClick(String fullShortUrl);

    /**
     * 根据url获取标题
     */
    String getTitleByUrl(String url);

    /**
     * 根据url获取图标favicon
     */
    String getFaviconUrl(String url);
}
