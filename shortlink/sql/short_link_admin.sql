SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- t_group 16 张分表
-- 分片键：username
-- 设计说明：
-- 1. uk_gid 支撑按 gid 广播更新、删除、排序时的快速定位
-- 2. uk_username_name_del 保证同一用户下分组名称唯一，并兼容逻辑删除
-- 3. idx_username_del_sort_update 支撑分组列表查询、最大分组数统计
-- ----------------------------
DROP PROCEDURE IF EXISTS create_t_group_tables;
DELIMITER $$
CREATE PROCEDURE create_t_group_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 16 DO
        SET @drop_sql = CONCAT('DROP TABLE IF EXISTS `t_group_', i, '`');
        PREPARE stmt FROM @drop_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @create_sql = CONCAT(
            'CREATE TABLE `t_group_', i, '` (',
            '  `id` bigint NOT NULL COMMENT ''ID'',',
            '  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''分组标识'',',
            '  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''分组名称'',',
            '  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''所属用户名'',',
            '  `sort_order` int NOT NULL DEFAULT 0 COMMENT ''分组排序'',',
            '  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',',
            '  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',',
            '  `del_flag` bigint NOT NULL DEFAULT 0 COMMENT ''删除标识 0-未删除 删除后为当前记录ID'',',
            '  PRIMARY KEY (`id`) USING BTREE,',
            '  UNIQUE KEY `uk_gid` (`gid`) USING BTREE,',
            '  UNIQUE KEY `uk_username_name_del` (`username`, `name`, `del_flag`) USING BTREE,',
            '  KEY `idx_username_del_sort_update` (`username`, `del_flag`, `sort_order` DESC, `update_time` DESC) USING BTREE',
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT=''用户分组表'' ROW_FORMAT=DYNAMIC'
        );
        PREPARE stmt FROM @create_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
CALL create_t_group_tables();
DROP PROCEDURE IF EXISTS create_t_group_tables;

-- ----------------------------
-- t_user 16 张分表
-- 分片键：username
-- 设计说明：
-- 1. uk_username_del 保证用户名唯一，并兼容逻辑删除
-- 2. idx_phone_del 支撑注册时手机号全库广播查重
-- 3. idx_mail_del 预留邮箱查重/找回密码等场景
-- ----------------------------
DROP PROCEDURE IF EXISTS create_t_user_tables;
DELIMITER $$
CREATE PROCEDURE create_t_user_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i < 16 DO
        SET @drop_sql = CONCAT('DROP TABLE IF EXISTS `t_user_', i, '`');
        PREPARE stmt FROM @drop_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @create_sql = CONCAT(
            'CREATE TABLE `t_user_', i, '` (',
            '  `id` bigint NOT NULL COMMENT ''ID'',',
            '  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''用户名'',',
            '  `password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs NOT NULL COMMENT ''密码'',',
            '  `real_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''真实姓名'',',
            '  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''手机号'',',
            '  `mail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs DEFAULT NULL COMMENT ''邮箱'',',
            '  `deletion_time` bigint DEFAULT NULL COMMENT ''注销时间戳'',',
            '  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',',
            '  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',',
            '  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''删除标识 0-未删除 1-已删除'',',
            '  PRIMARY KEY (`id`) USING BTREE,',
            '  UNIQUE KEY `uk_username_del` (`username`, `del_flag`) USING BTREE,',
            '  KEY `idx_phone_del` (`phone`, `del_flag`) USING BTREE,',
            '  KEY `idx_mail_del` (`mail`(255), `del_flag`) USING BTREE',
            ') ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs COMMENT=''用户表'' ROW_FORMAT=DYNAMIC'
        );
        PREPARE stmt FROM @create_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;
CALL create_t_user_tables();
DROP PROCEDURE IF EXISTS create_t_user_tables;

SET FOREIGN_KEY_CHECKS = 1;

