SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE short_link_admin;

DROP PROCEDURE IF EXISTS rebuild_short_link_admin_indexes;

DELIMITER $$

CREATE PROCEDURE rebuild_short_link_admin_indexes()
BEGIN
    -- 所有 DECLARE 必须放在最前面，且连续（无中间语句）
    DECLARE i INT DEFAULT 0;
    DECLARE idx_name VARCHAR(64);
    DECLARE done BOOLEAN DEFAULT FALSE;

    -- Handler 必须在所有变量声明之后、可执行语句之前
    DECLARE CONTINUE HANDLER FOR 1091
        BEGIN
            -- 可选：记录跳过信息（调试用）
            -- SELECT CONCAT('Index not exists, skipped: ', @idx_name) AS warning;
        END;

    -- 处理 t_group_0 ~ t_group_15
    SET i = 0;
    WHILE i < 16 DO
            SET @table_name = CONCAT('t_group_', i);

            -- 使用游标或循环删除索引（更安全方式：先查再删）
            -- 方案一：用 WHILE + SELECT INTO（需处理 NULL）
            SET @idx_name = NULL;
            SELECT INDEX_NAME INTO @idx_name
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = @table_name
              AND index_name <> 'PRIMARY'
            LIMIT 1;

            WHILE @idx_name IS NOT NULL DO
                    SET @drop_sql = CONCAT('ALTER TABLE `', @table_name, '` DROP INDEX `', @idx_name, '`');
                    PREPARE stmt FROM @drop_sql;
                    EXECUTE stmt;
                    DEALLOCATE PREPARE stmt;

                    -- 重新获取下一个索引
                    SET @idx_name = NULL;
                    SELECT INDEX_NAME INTO @idx_name
                    FROM information_schema.statistics
                    WHERE table_schema = DATABASE()
                      AND table_name = @table_name
                      AND index_name <> 'PRIMARY'
                    LIMIT 1;
                END WHILE;

            -- 重建索引
            SET @add_sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD UNIQUE INDEX `uk_gid` (`gid`), ',
                    'ADD UNIQUE INDEX `uk_username_name_del` (`username`, `name`, `del_flag`), ',
                    'ADD INDEX `idx_username_del_sort_update` (`username`, `del_flag`, `sort_order` DESC, `update_time` DESC)'
                           );
            PREPARE stmt FROM @add_sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET i = i + 1;
        END WHILE;

    -- 处理 t_user_0 ~ t_user_15
    SET i = 0;
    WHILE i < 16 DO
            SET @table_name = CONCAT('t_user_', i);

            SET @idx_name = NULL;
            SELECT INDEX_NAME INTO @idx_name
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = @table_name
              AND index_name <> 'PRIMARY'
            LIMIT 1;

            WHILE @idx_name IS NOT NULL DO
                    SET @drop_sql = CONCAT('ALTER TABLE `', @table_name, '` DROP INDEX `', @idx_name, '`');
                    PREPARE stmt FROM @drop_sql;
                    EXECUTE stmt;
                    DEALLOCATE PREPARE stmt;

                    SET @idx_name = NULL;
                    SELECT INDEX_NAME INTO @idx_name
                    FROM information_schema.statistics
                    WHERE table_schema = DATABASE()
                      AND table_name = @table_name
                      AND index_name <> 'PRIMARY'
                    LIMIT 1;
                END WHILE;

            -- 重建索引
            SET @add_sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD UNIQUE INDEX `uk_username_del` (`username`, `del_flag`), ',
                    'ADD INDEX `idx_phone_del` (`phone`, `del_flag`), ',
                    'ADD INDEX `idx_mail_del` (`mail`(255), `del_flag`)'
                           );
            PREPARE stmt FROM @add_sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET i = i + 1;
        END WHILE;

END$$

DELIMITER ;

-- 执行
CALL rebuild_short_link_admin_indexes();

-- 清理
DROP PROCEDURE IF EXISTS rebuild_short_link_admin_indexes;

SET FOREIGN_KEY_CHECKS = 1;