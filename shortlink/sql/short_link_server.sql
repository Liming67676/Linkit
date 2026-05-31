SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- t_link 16 张分表
-- 分片键：gid
-- 设计说明：
-- 1. uk_domain_short_uri_del 保证同一域名下短链接后缀唯一，并兼容逻辑删除后的重建
-- 2. idx_gid_origin_url_del 支撑同分组下原始链接去重校验
-- 3. idx_gid_enable_del_update 支撑短链接分页、回收站分页、分组下短链接列表
-- 4. idx_gid_full_short_url_active_valid 支撑短链接更新、恢复、跳转落库后的精确查询
-- 5. idx_gid_enable_del_valid 支撑过期清理任务按 gid + 有效期批量扫描
-- ----------------------------
DROP PROCEDURE IF EXISTS create_t_link_tables;
DELIMITER $$
CREATE PROCEDURE create_t_link_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 16 DO
        SET @drop_sql = CONCAT('DROP TABLE IF EXISTS `t_link_', i, '`');
        PREPARE stmt FROM @drop_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @create_sql = CONCAT(
            'CREATE TABLE `t_link_', i, '` (',
            '  `id` bigint NOT NULL COMMENT ''ID'',',
            '  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''短链接域名'',',
            '  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''短链接后缀'',',
            '  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''完整短链接'',',
            '  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''原始链接'',',
            '  `click_num` int NOT NULL DEFAULT 0 COMMENT ''点击量'',',
            '  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''分组标识'',',
            '  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''启用状态 0-启用 1-禁用/回收站'',',
            '  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT ''创建类型 0-接口 1-控制台'',',
            '  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''有效期类型 0-永久 1-自定义'',',
            '  `valid_date` datetime NOT NULL COMMENT ''有效期截止时间'',',
            '  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''短链接描述'',',
            '  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',',
            '  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',',
            '  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''删除标识 0-未删除 1-已删除'',',
            '  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''网站图标'',',
            '  PRIMARY KEY (`id`) USING BTREE,',
            '  UNIQUE KEY `uk_domain_short_uri_del` (`domain`, `short_uri`, `del_flag`) USING BTREE,',
            '  KEY `idx_gid_origin_url_del` (`gid`, `origin_url`(255), `del_flag`) USING BTREE,',
            '  KEY `idx_gid_enable_del_update` (`gid`, `enable_status`, `del_flag`, `update_time` DESC, `id`) USING BTREE,',
            '  KEY `idx_gid_full_short_url_active_valid` (`gid`, `full_short_url`, `enable_status`, `del_flag`, `valid_date`) USING BTREE,',
            '  KEY `idx_gid_enable_del_valid` (`gid`, `enable_status`, `del_flag`, `valid_date`, `id`) USING BTREE',
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT=''短链接主表'' ROW_FORMAT=DYNAMIC'
        );
        PREPARE stmt FROM @create_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
CALL create_t_link_tables();
DROP PROCEDURE IF EXISTS create_t_link_tables;

-- ----------------------------
-- t_link_access_log_stats 16 张分表
-- 分片键：full_short_url
-- 设计说明：
-- 1. idx_full_short_url_create_time 支撑访问记录分页、Top IP、时间范围统计
-- 2. idx_full_short_url_ip_create_time 支撑新老访客判断与指定 IP 历史访问判重
-- ----------------------------
DROP PROCEDURE IF EXISTS create_t_link_access_log_stats_tables;
DELIMITER $$
CREATE PROCEDURE create_t_link_access_log_stats_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 16 DO
        SET @drop_sql = CONCAT('DROP TABLE IF EXISTS `t_link_access_log_stats_', i, '`');
        PREPARE stmt FROM @drop_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @create_sql = CONCAT(
            'CREATE TABLE `t_link_access_log_stats_', i, '` (',
            '  `id` bigint NOT NULL COMMENT ''ID'',',
            '  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''完整短链接'',',
            '  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''访客标识'',',
            '  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''国家'',',
            '  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''省份'',',
            '  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''城市'',',
            '  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''地区编码'',',
            '  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''操作系统'',',
            '  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''浏览器'',',
            '  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''设备类型'',',
            '  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''网络类型'',',
            '  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''访问 IP'',',
            '  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''访客类型 newUser/oldUser'',',
            '  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',',
            '  PRIMARY KEY (`id`) USING BTREE,',
            '  KEY `idx_full_short_url_create_time` (`full_short_url`, `create_time` DESC, `id`) USING BTREE,',
            '  KEY `idx_full_short_url_ip_create_time` (`full_short_url`, `ip`, `create_time`) USING BTREE',
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT=''短链接访问日志统计表'' ROW_FORMAT=DYNAMIC'
        );
        PREPARE stmt FROM @create_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
CALL create_t_link_access_log_stats_tables();
DROP PROCEDURE IF EXISTS create_t_link_access_log_stats_tables;

-- ----------------------------
-- t_link_access_stats
-- 分钟级明细没有单独建表，这里按日 + 小时聚合
-- 设计说明：
-- 1. uk_full_short_url_date_hour_del 支撑同一短链接同一小时幂等累加
-- 2. idx_full_short_url_del_date 支撑按时间范围的 PV/UV/UIP 汇总与趋势统计
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_stats`;
CREATE TABLE `t_link_access_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `pv` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `uv` int NOT NULL DEFAULT 0 COMMENT '独立访客数',
  `uip` int NOT NULL DEFAULT 0 COMMENT '独立 IP 数',
  `date` date NOT NULL COMMENT '统计日期',
  `hour` int NOT NULL COMMENT '统计小时',
  `weekday` int NOT NULL COMMENT '星期几，1-7',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_hour_del` (`full_short_url`, `date`, `hour`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接访问基础统计表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- t_link_browser_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_browser_stats`;
CREATE TABLE `t_link_browser_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '浏览器',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_browser_del` (`full_short_url`, `date`, `browser`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接浏览器统计表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- t_link_device_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_device_stats`;
CREATE TABLE `t_link_device_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '设备类型',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_device_del` (`full_short_url`, `date`, `device`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接设备统计表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- t_link_goto 16 张分表
-- 分片键：full_short_url
-- 设计说明：
-- 1. uk_full_short_url 保证完整短链接到 gid 的全局映射在单分片内唯一
-- 2. idx_gid 支撑按 gid 清理或补偿时的辅助查询
-- ----------------------------
DROP PROCEDURE IF EXISTS create_t_link_goto_tables;
DELIMITER $$
CREATE PROCEDURE create_t_link_goto_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 16 DO
        SET @drop_sql = CONCAT('DROP TABLE IF EXISTS `t_link_goto_', i, '`');
        PREPARE stmt FROM @drop_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @create_sql = CONCAT(
            'CREATE TABLE `t_link_goto_', i, '` (',
            '  `id` bigint NOT NULL COMMENT ''ID'',',
            '  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''分组标识'',',
            '  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''完整短链接'',',
            '  PRIMARY KEY (`id`) USING BTREE,',
            '  UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE,',
            '  KEY `idx_gid` (`gid`) USING BTREE',
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT=''短链接跳转路由表'' ROW_FORMAT=DYNAMIC'
        );
        PREPARE stmt FROM @create_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
CALL create_t_link_goto_tables();
DROP PROCEDURE IF EXISTS create_t_link_goto_tables;

-- ----------------------------
-- t_link_locale_stats
-- 设计说明：
-- 1. uk_full_short_url_date_adcode_del 支撑按地区编码幂等累加
-- 2. idx_full_short_url_del_date 支撑按日期范围聚合省份分布
-- ----------------------------
DROP TABLE IF EXISTS `t_link_locale_stats`;
CREATE TABLE `t_link_locale_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '地区编码',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_adcode_del` (`full_short_url`, `date`, `adcode`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接地区统计表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- t_link_network_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_network_stats`;
CREATE TABLE `t_link_network_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '网络类型',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_network_del` (`full_short_url`, `date`, `network`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接网络统计表' ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- t_link_os_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_os_stats`;
CREATE TABLE `t_link_os_stats` (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '操作系统',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_full_short_url_date_os_del` (`full_short_url`, `date`, `os`, `del_flag`) USING BTREE,
  KEY `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT='短链接操作系统统计表' ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
