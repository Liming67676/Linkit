package org.hang.shortlink.project;

import org.junit.jupiter.api.Test;

public class ShardTableSqlGeneratorTest {

    @Test
    void createLinkAccessLogStatsTables() {
        for (int i = 0; i < 16; i++) {
            String table = "CREATE TABLE `t_link_access_log_stats_" + i + "` (\n" +
                    "  `id` bigint NOT NULL COMMENT 'ID',\n" +
                    "  `full_short_url` varchar(128) NOT NULL COMMENT '完整短链接',\n" +
                    "  `uv_type` varchar(32) DEFAULT NULL COMMENT 'uv类型',\n" +
                    "  `user` varchar(128) DEFAULT NULL COMMENT '用户标识',\n" +
                    "  `country` varchar(64) DEFAULT NULL COMMENT '国家',\n" +
                    "  `province` varchar(64) DEFAULT NULL COMMENT '省份',\n" +
                    "  `city` varchar(64) DEFAULT NULL COMMENT '城市',\n" +
                    "  `adcode` varchar(32) DEFAULT NULL COMMENT '区域代码',\n" +
                    "  `os` varchar(64) DEFAULT NULL COMMENT '操作系统',\n" +
                    "  `browser` varchar(64) DEFAULT NULL COMMENT '浏览器',\n" +
                    "  `device` varchar(32) DEFAULT NULL COMMENT '设备类型',\n" +
                    "  `network` varchar(32) DEFAULT NULL COMMENT '网络类型',\n" +
                    "  `ip` varchar(64) DEFAULT NULL COMMENT 'ip地址',\n" +
                    "  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  KEY `idx_full_short_url_create_time` (`full_short_url`, `create_time`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs;";
            System.out.println(table);
        }
    }
}
