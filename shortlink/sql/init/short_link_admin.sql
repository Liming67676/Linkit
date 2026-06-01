/*
 Navicat Premium Dump SQL

 Source Server         : shortlink-mysql
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : 127.0.0.1:3306
 Source Schema         : short_link_admin

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 01/06/2026 16:50:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_group_0
-- ----------------------------
DROP TABLE IF EXISTS `t_group_0`;
CREATE TABLE `t_group_0`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_1
-- ----------------------------
DROP TABLE IF EXISTS `t_group_1`;
CREATE TABLE `t_group_1`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_10
-- ----------------------------
DROP TABLE IF EXISTS `t_group_10`;
CREATE TABLE `t_group_10`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_11
-- ----------------------------
DROP TABLE IF EXISTS `t_group_11`;
CREATE TABLE `t_group_11`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_12
-- ----------------------------
DROP TABLE IF EXISTS `t_group_12`;
CREATE TABLE `t_group_12`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_13
-- ----------------------------
DROP TABLE IF EXISTS `t_group_13`;
CREATE TABLE `t_group_13`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_14
-- ----------------------------
DROP TABLE IF EXISTS `t_group_14`;
CREATE TABLE `t_group_14`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_15
-- ----------------------------
DROP TABLE IF EXISTS `t_group_15`;
CREATE TABLE `t_group_15`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_2
-- ----------------------------
DROP TABLE IF EXISTS `t_group_2`;
CREATE TABLE `t_group_2`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_3
-- ----------------------------
DROP TABLE IF EXISTS `t_group_3`;
CREATE TABLE `t_group_3`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_4
-- ----------------------------
DROP TABLE IF EXISTS `t_group_4`;
CREATE TABLE `t_group_4`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_5
-- ----------------------------
DROP TABLE IF EXISTS `t_group_5`;
CREATE TABLE `t_group_5`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_6
-- ----------------------------
DROP TABLE IF EXISTS `t_group_6`;
CREATE TABLE `t_group_6`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_7
-- ----------------------------
DROP TABLE IF EXISTS `t_group_7`;
CREATE TABLE `t_group_7`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_8
-- ----------------------------
DROP TABLE IF EXISTS `t_group_8`;
CREATE TABLE `t_group_8`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_group_9
-- ----------------------------
DROP TABLE IF EXISTS `t_group_9`;
CREATE TABLE `t_group_9`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组标识',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '分组名称',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '所属用户名',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '分组排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_gid`(`gid` ASC) USING BTREE,
  UNIQUE INDEX `uk_username_name_del`(`username` ASC, `name` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_username_del_sort_update`(`username` ASC, `del_flag` ASC, `sort_order` DESC, `update_time` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户分组表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_0
-- ----------------------------
DROP TABLE IF EXISTS `t_user_0`;
CREATE TABLE `t_user_0`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_1
-- ----------------------------
DROP TABLE IF EXISTS `t_user_1`;
CREATE TABLE `t_user_1`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_10
-- ----------------------------
DROP TABLE IF EXISTS `t_user_10`;
CREATE TABLE `t_user_10`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_11
-- ----------------------------
DROP TABLE IF EXISTS `t_user_11`;
CREATE TABLE `t_user_11`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_12
-- ----------------------------
DROP TABLE IF EXISTS `t_user_12`;
CREATE TABLE `t_user_12`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_13
-- ----------------------------
DROP TABLE IF EXISTS `t_user_13`;
CREATE TABLE `t_user_13`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_14
-- ----------------------------
DROP TABLE IF EXISTS `t_user_14`;
CREATE TABLE `t_user_14`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_15
-- ----------------------------
DROP TABLE IF EXISTS `t_user_15`;
CREATE TABLE `t_user_15`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_2
-- ----------------------------
DROP TABLE IF EXISTS `t_user_2`;
CREATE TABLE `t_user_2`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_3
-- ----------------------------
DROP TABLE IF EXISTS `t_user_3`;
CREATE TABLE `t_user_3`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_4
-- ----------------------------
DROP TABLE IF EXISTS `t_user_4`;
CREATE TABLE `t_user_4`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_5
-- ----------------------------
DROP TABLE IF EXISTS `t_user_5`;
CREATE TABLE `t_user_5`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_6
-- ----------------------------
DROP TABLE IF EXISTS `t_user_6`;
CREATE TABLE `t_user_6`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_7
-- ----------------------------
DROP TABLE IF EXISTS `t_user_7`;
CREATE TABLE `t_user_7`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_8
-- ----------------------------
DROP TABLE IF EXISTS `t_user_8`;
CREATE TABLE `t_user_8`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_9
-- ----------------------------
DROP TABLE IF EXISTS `t_user_9`;
CREATE TABLE `t_user_9`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '用户名',
  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT '密码',
  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '手机号',
  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NULL DEFAULT NULL COMMENT '邮箱',
  `deletion_time` bigint NULL DEFAULT NULL COMMENT '注销时间戳',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username_del`(`username` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_phone_del`(`phone` ASC, `del_flag` ASC) USING BTREE,
  INDEX `idx_mail_del`(`mail`(255) ASC, `del_flag` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_cs COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
