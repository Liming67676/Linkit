SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE short_link_server;

-- 为 t_link_* 分表增加同分组原始链接唯一约束。
-- origin_url 长度为 1024，直接用前缀唯一索引会误判不同长链接；因此使用 SHA2 生成列做精确去重。
DROP PROCEDURE IF EXISTS add_origin_url_unique_index;
DELIMITER $$

CREATE PROCEDURE add_origin_url_unique_index()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE column_count INT DEFAULT 0;
    DECLARE index_count INT DEFAULT 0;

    WHILE i < 16 DO
        SET @table_name = CONCAT('t_link_', i);

        SELECT COUNT(1)
        INTO column_count
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = @table_name
          AND column_name = 'origin_url_hash';

        IF column_count = 0 THEN
            SET @sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD COLUMN `origin_url_hash` char(64) GENERATED ALWAYS AS (sha2(`origin_url`, 256)) STORED COMMENT ''原始链接SHA256'' AFTER `origin_url`'
                       );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        SELECT COUNT(1)
        INTO index_count
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = @table_name
          AND index_name = 'uk_gid_origin_url_hash_del';

        IF index_count = 0 THEN
            SET @sql = CONCAT(
                    'ALTER TABLE `', @table_name, '` ',
                    'ADD UNIQUE INDEX `uk_gid_origin_url_hash_del` (`gid`, `origin_url_hash`, `del_flag`)'
                       );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;

        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL add_origin_url_unique_index();
DROP PROCEDURE IF EXISTS add_origin_url_unique_index;

SET FOREIGN_KEY_CHECKS = 1;
