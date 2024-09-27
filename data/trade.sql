SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for symbol_info
-- ----------------------------
DROP TABLE IF EXISTS `symbol_info`;
CREATE TABLE `symbol_info`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `symbol` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标的简称',
  `symbol_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标的全称',
  `tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '所属板块',
  `institution` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操盘机构',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明信息',
  `rank` int(10) NULL DEFAULT NULL COMMENT '排名',
  `circulating_supply` decimal(40, 4) NULL DEFAULT NULL COMMENT '当前流通量',
  `total_supply` decimal(40, 4) NULL DEFAULT NULL COMMENT '当前总量',
  `max_supply` decimal(40, 4) NULL DEFAULT NULL COMMENT '总供应量',
  `create_time` datetime NULL DEFAULT NULL COMMENT '添加时间',
  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '标的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for volume_info
-- ----------------------------
DROP TABLE IF EXISTS `volume_info`;
CREATE TABLE `volume_info`  (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `group_flag` bigint(20) NULL DEFAULT NULL COMMENT '分组标记',
  `symbol` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标的',
  `quote_symbol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易对',
  `current_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '当前价格',
  `exchange_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易平台',
  `exchange_proportion` decimal(10, 4) NULL DEFAULT NULL COMMENT '平台占比',
  `exchange_volume` decimal(20, 4) NULL DEFAULT NULL COMMENT '平台交易量',
  `level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '时间级别',
  `effective_liquidity` bigint(20) NULL DEFAULT NULL COMMENT '流行性分数',
  `market_reputation` int(10) NULL DEFAULT NULL COMMENT '置信度',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `del_flag` tinyint(1) NULL DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `symbol_index`(`symbol`) USING BTREE COMMENT '标的索引',
  INDEX `group_flag_index`(`group_flag`) USING BTREE COMMENT '标记索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '交易量信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
