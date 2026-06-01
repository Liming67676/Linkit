SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE short_link_server;

DROP PROCEDURE IF EXISTS rebuild_short_link_server_indexes;
DELIMITER $$

CREATE PROCEDURE rebuild_short_link_server_indexes()
BEGIN
    DECLARE i INT DEFAULT 0;

    WHILE i < 16 DO
            SET @table_name = CONCAT('t_link_', i);

            SET @drop_indexes_sql = NULL;
            SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
            INTO @drop_indexes_sql
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = @table_name
              AND index_name <> 'PRIMARY';

            IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
                SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
                PREPARE stmt FROM @sql;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;
            END IF;

            SET @sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD UNIQUE INDEX `uk_domain_short_uri_del` (`domain`, `short_uri`, `del_flag`), ',
                    'ADD INDEX `idx_gid_origin_url_del` (`gid`, `origin_url`(255), `del_flag`), ',
                    'ADD INDEX `idx_gid_enable_del_update` (`gid`, `enable_status`, `del_flag`, `update_time` DESC, `id`), ',
                    'ADD INDEX `idx_gid_full_short_url_active_valid` (`gid`, `full_short_url`, `enable_status`, `del_flag`, `valid_date`), ',
                    'ADD INDEX `idx_gid_enable_del_valid` (`gid`, `enable_status`, `del_flag`, `valid_date`, `id`)'
                       );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET i = i + 1;
        END WHILE;

    SET i = 0;
    WHILE i < 16 DO
            SET @table_name = CONCAT('t_link_access_log_stats_', i);

            SET @drop_indexes_sql = NULL;
            SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
            INTO @drop_indexes_sql
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = @table_name
              AND index_name <> 'PRIMARY';

            IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
                SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
                PREPARE stmt FROM @sql;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;
            END IF;

            SET @sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD INDEX `idx_full_short_url_create_time` (`full_short_url`, `create_time` DESC, `id`), ',
                    'ADD INDEX `idx_full_short_url_ip_create_time` (`full_short_url`, `ip`, `create_time`)'
                       );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET i = i + 1;
        END WHILE;

    SET @table_name = 't_link_access_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_access_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_hour_del` (`full_short_url`, `date`, `hour`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);

    SET @table_name = 't_link_browser_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_browser_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_browser_del` (`full_short_url`, `date`, `browser`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);

    SET @table_name = 't_link_device_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_device_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_device_del` (`full_short_url`, `date`, `device`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);

    SET i = 0;
    WHILE i < 16 DO
            SET @table_name = CONCAT('t_link_goto_', i);

            SET @drop_indexes_sql = NULL;
            SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
            INTO @drop_indexes_sql
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = @table_name
              AND index_name <> 'PRIMARY';

            IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
                SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
                PREPARE stmt FROM @sql;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;
            END IF;

            SET @sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD UNIQUE INDEX `uk_full_short_url` (`full_short_url`), ',
                    'ADD INDEX `idx_gid` (`gid`)'
                       );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            SET i = i + 1;
        END WHILE;

    SET @table_name = 't_link_locale_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_locale_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_adcode_del` (`full_short_url`, `date`, `adcode`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);

    SET @table_name = 't_link_network_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_network_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_network_del` (`full_short_url`, `date`, `network`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);

    SET @table_name = 't_link_os_stats';
    SET @drop_indexes_sql = NULL;
    SELECT GROUP_CONCAT(DISTINCT CONCAT('DROP INDEX `', INDEX_NAME, '`') ORDER BY INDEX_NAME SEPARATOR ', ')
    INTO @drop_indexes_sql
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = @table_name
      AND index_name <> 'PRIMARY';

    IF @drop_indexes_sql IS NOT NULL AND LENGTH(@drop_indexes_sql) > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', @table_name, '` ', @drop_indexes_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;

    ALTER TABLE `t_link_os_stats`
        ADD UNIQUE INDEX `uk_full_short_url_date_os_del` (`full_short_url`, `date`, `os`, `del_flag`),
        ADD INDEX `idx_full_short_url_del_date` (`full_short_url`, `del_flag`, `date`);
END$$

DELIMITER ;

CALL rebuild_short_link_server_indexes();
DROP PROCEDURE IF EXISTS rebuild_short_link_server_indexes;

SET FOREIGN_KEY_CHECKS = 1;