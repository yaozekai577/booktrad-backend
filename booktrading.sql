/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : booktrading

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 01/05/2026 09:04:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint NOT NULL COMMENT '会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：user/assistant',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `ai_chat_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `ai_chat_session` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 400 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ai_chat_session
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_session`;
CREATE TABLE `ai_chat_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AI助手对话' COMMENT '会话标题',
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int NULL DEFAULT 0 COMMENT '会话状态：0-正常对话 1-发布求购中',
  `temp_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '临时存储求购信息（JSON格式）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  CONSTRAINT `fk_ai_session_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '书籍ID，主键',
  `seller_id` bigint UNSIGNED NOT NULL COMMENT '卖家用户ID（关联 user 表）',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'ISBN编号',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '书籍分类ID',
  `price` decimal(10, 2) NOT NULL COMMENT '出售价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `book_condition` tinyint UNSIGNED NOT NULL COMMENT '成色：1-全新 2-九成新 3-八成新 4-明显使用痕迹',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '书籍描述（使用情况、备注等）',
  `cover_image` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-在售 2-已下架 3-交易中 4-已售出',
  `is_banned` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否封禁：0-正常，1-封禁',
  `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间（软删除）',
  `trade_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '交易方式：自取/邮寄 → tradeType',
  `preferred_location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '卖家期望交易地点',
  `defect_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '瑕疵说明 → defectDesc',
  `publish_time` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '出版时间 yyyy-MM → publishTime',
  `category_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '分类路径 → categoryPath',
  `sold_at` datetime NULL DEFAULT NULL COMMENT '成交时间',
  `ban_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封禁原因',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_price`(`price` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_book_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '二手书籍表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_category
-- ----------------------------
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父分类ID',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `book_category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_favorite
-- ----------------------------
DROP TABLE IF EXISTS `book_favorite`;
CREATE TABLE `book_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-已收藏 0-已取消',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_book`(`user_id` ASC, `book_id` ASC) USING BTREE COMMENT '用户书籍唯一索引',
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_fav_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for book_order
-- ----------------------------
DROP TABLE IF EXISTS `book_order`;
CREATE TABLE `book_order`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `buyer_id` bigint UNSIGNED NOT NULL COMMENT '买家用户ID',
  `seller_id` bigint UNSIGNED NOT NULL COMMENT '卖家用户ID',
  `book_id` bigint UNSIGNED NOT NULL COMMENT '书籍ID',
  `wanted_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联求购ID，普通图书订单为0',
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '书籍标题（冗余）',
  `book_cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '书籍封面（冗余）',
  `original_price` decimal(10, 2) NOT NULL COMMENT '书籍原价（冗余）',
  `price` decimal(10, 2) NOT NULL COMMENT '成交价',
  `trade_type` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '交易方式：1线下面交',
  `meet_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '约定面交地点',
  `meet_time` datetime NULL DEFAULT NULL COMMENT '约定面交时间',
  `buyer_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '买家联系电话（冗余）',
  `seller_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '卖家联系电话（冗余）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单备注',
  `cancel_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态：1待确认 2已确认、待交易 3已完成 4已取消',
  `buyer_confirmed` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '买家是否确认收货：0未确认 1已确认',
  `seller_confirmed` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '卖家是否确认交货：0未确认 1已确认',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `confirmed_at` datetime NULL DEFAULT NULL COMMENT '卖家确认接单时间',
  `buyer_confirmed_at` datetime NULL DEFAULT NULL COMMENT '买家确认收货时间',
  `seller_confirmed_at` datetime NULL DEFAULT NULL COMMENT '卖家确认交货时间',
  `buyer_reviewed` tinyint NULL DEFAULT 0 COMMENT '买家是否已评价：0未评价 1已评价',
  `seller_reviewed` tinyint NULL DEFAULT 0 COMMENT '卖家是否已评价：0未评价 1已评价',
  `completed_at` datetime NULL DEFAULT NULL COMMENT '订单完成时间（双方都确认后）',
  `cancelled_at` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '二手书订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint UNSIGNED NOT NULL COMMENT '会话ID',
  `sender_id` bigint UNSIGNED NOT NULL COMMENT '发送者用户ID',
  `message_type` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '消息类型：1文本 2图片 3系统消息',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  CONSTRAINT `fk_chat_msg_session` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_chat_msg_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `book_id` bigint UNSIGNED NOT NULL COMMENT '关联书籍ID',
  `wanted_id` bigint NOT NULL DEFAULT 0 COMMENT '关联求购ID，非求购会话为0',
  `buyer_id` bigint UNSIGNED NOT NULL COMMENT '买家用户ID',
  `seller_id` bigint UNSIGNED NOT NULL COMMENT '卖家用户ID',
  `last_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息摘要',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1正常 2关闭',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_book_wanted_buyer_seller`(`book_id` ASC, `wanted_id` ASC, `buyer_id` ASC, `seller_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  CONSTRAINT `fk_chat_session_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_chat_session_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_chat_session_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_review
-- ----------------------------
DROP TABLE IF EXISTS `order_review`;
CREATE TABLE `order_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `reviewer_id` bigint NOT NULL COMMENT '评价人ID',
  `reviewee_id` bigint NOT NULL COMMENT '被评价人ID',
  `reviewer_role` tinyint NOT NULL COMMENT '评价人角色：1买家评价卖家 2卖家评价买家',
  `rating` tinyint NOT NULL COMMENT '评分：1-5星',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名：0否 1是',
  `reply_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复内容（被评价人可以回复）',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_reviewer`(`order_id` ASC, `reviewer_id` ASC) USING BTREE COMMENT '一个订单中每人只能评价一次',
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_reviewer_id`(`reviewer_id` ASC) USING BTREE,
  INDEX `idx_reviewee_id`(`reviewee_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `book_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewee` FOREIGN KEY (`reviewee_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for search_history
-- ----------------------------
DROP TABLE IF EXISTS `search_history`;
CREATE TABLE `search_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID（未登录为NULL）',
  `keyword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '搜索关键词',
  `search_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_search_time`(`search_time` ASC) USING BTREE,
  CONSTRAINT `fk_search_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '搜索历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名，用于登录',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，加密存储',
  `seller_rating_score` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '作为卖家的评分：0-5分',
  `seller_rating_count` int NULL DEFAULT 0 COMMENT '作为卖家被评价次数',
  `buyer_rating_score` decimal(3, 2) NULL DEFAULT 5.00 COMMENT '作为买家的评分：0-5分',
  `buyer_rating_count` int NULL DEFAULT 0 COMMENT '作为买家被评价次数',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `role` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：0-封禁，1-正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '删除时间，用于软删除',
  `extra_info` json NULL COMMENT '扩展信息，用于存储额外用户信息，如头像、联系方式、学院、年级等',
  `ban_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封禁原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted_at`(`deleted_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wanted_request
-- ----------------------------
DROP TABLE IF EXISTS `wanted_request`;
CREATE TABLE `wanted_request`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '求购ID',
  `buyer_id` bigint UNSIGNED NOT NULL COMMENT '发布人用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '求购书名',
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者',
  `category_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '书籍分类ID',
  `budget` decimal(10, 2) NOT NULL COMMENT '预算金额',
  `desired_condition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期望成色',
  `expected_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期望交易地点',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '需求说明',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系手机号',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态：1-进行中 2-已关闭',
  `close_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭原因',
  `closed_at` datetime NULL DEFAULT NULL COMMENT '关闭时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime NULL DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  CONSTRAINT `fk_wanted_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wanted_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '求购信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
