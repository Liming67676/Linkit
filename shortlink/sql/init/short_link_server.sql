/*
 Navicat Premium Dump SQL

 Source Server         : shortlink-mysql
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : 127.0.0.1:3306
 Source Schema         : short_link_server

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 02/06/2026 11:58:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_link_0
-- ----------------------------
DROP TABLE IF EXISTS `t_link_0`;
CREATE TABLE `t_link_0`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_1
-- ----------------------------
DROP TABLE IF EXISTS `t_link_1`;
CREATE TABLE `t_link_1`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_10
-- ----------------------------
DROP TABLE IF EXISTS `t_link_10`;
CREATE TABLE `t_link_10`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_11
-- ----------------------------
DROP TABLE IF EXISTS `t_link_11`;
CREATE TABLE `t_link_11`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_12
-- ----------------------------
DROP TABLE IF EXISTS `t_link_12`;
CREATE TABLE `t_link_12`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_13
-- ----------------------------
DROP TABLE IF EXISTS `t_link_13`;
CREATE TABLE `t_link_13`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_14
-- ----------------------------
DROP TABLE IF EXISTS `t_link_14`;
CREATE TABLE `t_link_14`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_15
-- ----------------------------
DROP TABLE IF EXISTS `t_link_15`;
CREATE TABLE `t_link_15`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_2
-- ----------------------------
DROP TABLE IF EXISTS `t_link_2`;
CREATE TABLE `t_link_2`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_3
-- ----------------------------
DROP TABLE IF EXISTS `t_link_3`;
CREATE TABLE `t_link_3`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_4
-- ----------------------------
DROP TABLE IF EXISTS `t_link_4`;
CREATE TABLE `t_link_4`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_5
-- ----------------------------
DROP TABLE IF EXISTS `t_link_5`;
CREATE TABLE `t_link_5`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_6
-- ----------------------------
DROP TABLE IF EXISTS `t_link_6`;
CREATE TABLE `t_link_6`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_7
-- ----------------------------
DROP TABLE IF EXISTS `t_link_7`;
CREATE TABLE `t_link_7`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_8
-- ----------------------------
DROP TABLE IF EXISTS `t_link_8`;
CREATE TABLE `t_link_8`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_9
-- ----------------------------
DROP TABLE IF EXISTS `t_link_9`;
CREATE TABLE `t_link_9`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接域名',
  `short_uri` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '短链接后缀',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `origin_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '原始链接',
  `origin_url_hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs GENERATED ALWAYS AS (sha2(`origin_url`,256)) STORED COMMENT '原始链接SHA256' NULL,
  `click_num` int NOT NULL DEFAULT 0 COMMENT '点击量',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `enable_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '启用状态 0-启用 1-禁用/回收站',
  `created_type` tinyint(1) NOT NULL DEFAULT 1 COMMENT '创建类型 0-接口 1-控制台',
  `valid_date_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '有效期类型 0-永久 1-自定义',
  `valid_date` datetime NOT NULL COMMENT '有效期截止时间',
  `describe` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '短链接描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 删除后为当前记录ID',
  `favicon` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网站图标',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_domain_short_uri_del`(`domain` ASC, `short_uri` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_origin_url_del`(`gid` ASC, `origin_url`(255) ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_update`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `update_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_gid_full_short_url_active_valid`(`gid` ASC, `full_short_url` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC) USING BTREE,
  INDEX `idx_gid_enable_del_valid`(`gid` ASC, `enable_status` ASC, `del_flag` ASC, `valid_date` ASC, `id` ASC) USING BTREE,
  UNIQUE INDEX `uk_gid_origin_url_hash_del`(`gid` ASC, `origin_url_hash` ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_0
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_0`;
CREATE TABLE `t_link_access_log_stats_0`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_1
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_1`;
CREATE TABLE `t_link_access_log_stats_1`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_10
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_10`;
CREATE TABLE `t_link_access_log_stats_10`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_11
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_11`;
CREATE TABLE `t_link_access_log_stats_11`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_12
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_12`;
CREATE TABLE `t_link_access_log_stats_12`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_13
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_13`;
CREATE TABLE `t_link_access_log_stats_13`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_14
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_14`;
CREATE TABLE `t_link_access_log_stats_14`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_15
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_15`;
CREATE TABLE `t_link_access_log_stats_15`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_2
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_2`;
CREATE TABLE `t_link_access_log_stats_2`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_3
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_3`;
CREATE TABLE `t_link_access_log_stats_3`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_4
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_4`;
CREATE TABLE `t_link_access_log_stats_4`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_5
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_5`;
CREATE TABLE `t_link_access_log_stats_5`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_6
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_6`;
CREATE TABLE `t_link_access_log_stats_6`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_7
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_7`;
CREATE TABLE `t_link_access_log_stats_7`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_8
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_8`;
CREATE TABLE `t_link_access_log_stats_8`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_log_stats_9
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_log_stats_9`;
CREATE TABLE `t_link_access_log_stats_9`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客标识',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '地区编码',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '浏览器',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '设备类型',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '网络类型',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访问 IP',
  `uv_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '访客类型 newUser/oldUser',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_full_short_url_create_time`(`full_short_url` ASC, `create_time` DESC, `id` ASC) USING BTREE,
  INDEX `idx_full_short_url_ip_create_time`(`full_short_url` ASC, `ip` ASC, `create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问日志统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_access_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_access_stats`;
CREATE TABLE `t_link_access_stats`  (
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
  UNIQUE INDEX `uk_full_short_url_date_hour_del`(`full_short_url` ASC, `date` ASC, `hour` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接访问基础统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_browser_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_browser_stats`;
CREATE TABLE `t_link_browser_stats`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `browser` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '浏览器',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url_date_browser_del`(`full_short_url` ASC, `date` ASC, `browser` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接浏览器统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_device_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_device_stats`;
CREATE TABLE `t_link_device_stats`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `device` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '设备类型',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url_date_device_del`(`full_short_url` ASC, `date` ASC, `device` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接设备统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_0
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_0`;
CREATE TABLE `t_link_goto_0`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_1
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_1`;
CREATE TABLE `t_link_goto_1`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_10
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_10`;
CREATE TABLE `t_link_goto_10`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_11
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_11`;
CREATE TABLE `t_link_goto_11`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_12
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_12`;
CREATE TABLE `t_link_goto_12`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_13
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_13`;
CREATE TABLE `t_link_goto_13`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_14
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_14`;
CREATE TABLE `t_link_goto_14`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_15
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_15`;
CREATE TABLE `t_link_goto_15`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_2
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_2`;
CREATE TABLE `t_link_goto_2`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_3
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_3`;
CREATE TABLE `t_link_goto_3`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_4
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_4`;
CREATE TABLE `t_link_goto_4`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_5
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_5`;
CREATE TABLE `t_link_goto_5`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_6
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_6`;
CREATE TABLE `t_link_goto_6`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_7
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_7`;
CREATE TABLE `t_link_goto_7`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_8
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_8`;
CREATE TABLE `t_link_goto_8`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_goto_9
-- ----------------------------
DROP TABLE IF EXISTS `t_link_goto_9`;
CREATE TABLE `t_link_goto_9`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url`(`full_short_url` ASC) USING BTREE,
  INDEX `idx_gid`(`gid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接跳转路由表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_locale_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_locale_stats`;
CREATE TABLE `t_link_locale_stats`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `country` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '城市',
  `adcode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '地区编码',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url_date_adcode_del`(`full_short_url` ASC, `date` ASC, `adcode` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接地区统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_network_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_network_stats`;
CREATE TABLE `t_link_network_stats`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `network` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '网络类型',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url_date_network_del`(`full_short_url` ASC, `date` ASC, `network` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接网络统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_link_os_stats
-- ----------------------------
DROP TABLE IF EXISTS `t_link_os_stats`;
CREATE TABLE `t_link_os_stats`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '完整短链接',
  `cnt` int NOT NULL DEFAULT 0 COMMENT '访问量',
  `os` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '操作系统',
  `date` date NOT NULL COMMENT '统计日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_full_short_url_date_os_del`(`full_short_url` ASC, `date` ASC, `os` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_full_short_url_del_date`(`full_short_url` ASC, `del_flag` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '短链接操作系统统计表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
