SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(255) NULL DEFAULT NULL,
  `inventory_cnt` bigint(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES (1, 1, 99);
INSERT INTO `inventory` VALUES (2, 2, 500);


-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `price` double(10, 2) NULL DEFAULT NULL,
  `pictureList` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `specification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `service` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `color` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `size` double NULL DEFAULT NULL,
  `shop_id` bigint(20) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Records of product_info
-- ----------------------------
INSERT INTO `product_info` VALUES (1, 'iphone11', 9999.00, NULL, '很酷的手机-wangming', NULL, '玫瑰金', 6.5, 1, '2020-03-15 21:04:14', '2020-03-15 20:04:18');
INSERT INTO `product_info` VALUES (2, 'vivo', 5000.00, '图片加载中', 'x20', NULL, '粉红', 6.5, 3, '2020-03-15 20:04:21', '2020-03-15 20:04:24');
INSERT INTO `product_info` VALUES (3, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (4, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (5, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (6, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (7, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (8, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (9, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');
INSERT INTO `product_info` VALUES (10, 'red mi', 2000.00, NULL, '青春版', NULL, '黑色', 6, 2, '2020-03-19 23:50:12', '2020-03-19 23:50:14');

-- ----------------------------
-- Table structure for shop_info
-- ----------------------------
DROP TABLE IF EXISTS `shop_info`;
CREATE TABLE `shop_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `level` int(255) NULL DEFAULT NULL,
  `good_comment_rate` double(255, 0) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_info
-- ----------------------------
INSERT INTO `shop_info` VALUES (1, '小汪手机店', 5, 1, '2020-03-15 20:04:35', '2020-03-15 20:04:39');
INSERT INTO `shop_info` VALUES (2, '青青5s店', 1, 1, '2020-03-15 20:04:35', '2020-03-15 20:04:55');
INSERT INTO `shop_info` VALUES (3, '涂山红红', 2, 1, '2020-03-15 20:04:35', '2020-03-15 20:04:56');
INSERT INTO `shop_info` VALUES (4, '涂山苏苏', 3, 1, '2020-03-15 20:04:35', '2020-03-15 20:05:12');


-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `age` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'wangming', 29);

SET FOREIGN_KEY_CHECKS = 1;
