package org.hang.shortlink.admin;

import org.hang.shortlink.admin.properties.ShortLinkProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@EnableConfigurationProperties(ShortLinkProperties.class)
public class DemoTest {

    @Test
    void testCreateUserTable() {
        // 循环生成 0~15 共16张分表
        for (int i = 0; i < 16; i++) {
            // 【核心修复】把SQL写在循环内，每次根据i重新拼接表名
            String table = "CREATE TABLE `t_user_" + i + "` (\n" +
                    "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
                    "  `username` varchar(256) DEFAULT NULL COMMENT '用户名',\n" +
                    "  `password` varchar(512) DEFAULT NULL COMMENT '密码',\n" +
                    "  `real_name` varchar(256) DEFAULT NULL COMMENT '真实姓名',\n" +
                    "  `phone` varchar(128) DEFAULT NULL COMMENT '手机号',\n" +
                    "  `mail` varchar(512) DEFAULT NULL COMMENT '邮箱',\n" +
                    "  `deletion_time` bigint DEFAULT NULL COMMENT '注销时间戳',\n" +
                    "  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                    "  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',\n" +
                    "  `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标识 0：未删除 1：已删除',\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
            System.out.println(table);
        }
    }
    @Test
    void createGroupTable() {
        for(int i = 0 ;i < 16 ; i++){
            String table = "CREATE TABLE `t_group_"+ i +"` (\n" +
                    "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
                    "  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',\n" +
                    "  `name` varchar(64) DEFAULT NULL COMMENT '分组名称',\n" +
                    "  `username` varchar(256) DEFAULT NULL COMMENT '创建分组用户名',\n" +
                    "  `sort_order` int DEFAULT NULL COMMENT '分组排序',\n" +
                    "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                    "  `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
                    "  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0：未删除 1：已删除',\n" +
                    "  PRIMARY KEY (`id`),\n" +
                    "  UNIQUE KEY `idx_unique_gid` (`gid`) USING BTREE\n" +
                    ") ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;";
            System.out.println(table);
        }
    }

    @Autowired
    private ShortLinkProperties shortLinkProperties;

    @Test
    void testProperties() {
        System.out.println(shortLinkProperties.getMaxGroup());
    }
}