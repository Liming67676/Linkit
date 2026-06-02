SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

USE short_link_admin;

DROP PROCEDURE IF EXISTS fix_group_logic_delete_del_flag;

DELIMITER $$

CREATE PROCEDURE fix_group_logic_delete_del_flag()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE CONTINUE HANDLER FOR 1091 BEGIN END;

    WHILE i < 16 DO
        SET @table_name = CONCAT('t_group_', i);

        SET @drop_index_sql = CONCAT('ALTER TABLE `', @table_name, '` DROP INDEX `uk_username_name_del`');
        PREPARE stmt FROM @drop_index_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @modify_column_sql = CONCAT(
                'ALTER TABLE `', @table_name, '` ',
                'MODIFY COLUMN `del_flag` bigint NOT NULL DEFAULT 0 COMMENT ''删除标识 0-未删除 删除后为当前记录ID'''
                                 );
        PREPARE stmt FROM @modify_column_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @repair_deleted_sql = CONCAT('UPDATE `', @table_name, '` SET `del_flag` = `id` WHERE `del_flag` <> 0');
        PREPARE stmt FROM @repair_deleted_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET @add_index_sql = CONCAT(
                'ALTER TABLE `', @table_name, '` ',
                'ADD UNIQUE INDEX `uk_username_name_del` (`username`, `name`, `del_flag`) USING BTREE'
                             );
        PREPARE stmt FROM @add_index_sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

CALL fix_group_logic_delete_del_flag();

DROP PROCEDURE IF EXISTS fix_group_logic_delete_del_flag;

SET FOREIGN_KEY_CHECKS = 1;
