package org.hang.shortlink.project.pojo.dto;

import lombok.Data;

/**
 * 高德IP定位接口响应实体
 */
@Data
public class GaoDeIpLocationDTO {
    /**
     * 状态值：1 成功，0 失败
     */
    private String status;
    /**
     * 响应信息
     */
    private String info;
    /**
     * 响应码：10000 成功
     */
    private String infocode;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 城市编码
     */
    private String adcode;
    /**
     * 经纬度坐标
     */
    private String rectangle;
}