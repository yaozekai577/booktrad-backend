# BookTrad - 二手书籍交易平台后端

## 项目简介

BookTrad 是一个基于 Spring Boot 3.x 的二手书籍交易平台后端系统，为用户提供书籍发布、求购、交易、评价、即时聊天等完整功能，同时集成了 AI 助手和 Google Books API 提供智能服务。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存/会话存储 |
| JWT | 0.12.5 | 身份认证 |
| WebSocket | - | 实时通信 |
| 阿里云 OSS | 3.17.1 | 文件存储 |
| 阿里云通义千问 | 2.12.0 | AI 服务 |
| OkHttp | 4.12.0 | HTTP 客户端 |
| Lombok | - | 代码简化 |

## 项目结构

```
com.booktrad
├── BooktradApplication.java          # 启动类
├── ai                                # AI 功能模块
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── service
│   └── vo
├── book                              # 书籍管理模块
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── service
│   └── vo
├── chat                              # 聊天模块
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── service
│   ├── vo
│   └── websocket
├── common                            # 公共组件
│   ├── context
│   ├── result
│   └── utils
├── config                            # 配置类
├── interceptor                       # 拦截器
├── order                             # 订单/评价模块
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── service
│   └── vo
├── user                              # 用户管理模块
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── service
│   └── vo
└── wanted                            # 求购管理模块
    ├── controller
    ├── dto
    ├── entity
    ├── mapper
    ├── service
    └── vo
```

## 核心功能

### 1. 用户管理
- 用户注册/登录（JWT 认证）
- 用户信息管理
- 密码修改
- 卖家/买家评分系统

### 2. 书籍管理
- 书籍发布与编辑
- 书籍搜索与筛选
- 书籍上架/下架
- 浏览记录
- 阿里云 OSS 文件上传

### 3. 订单管理
- 创建订单
- 订单确认
- 订单交易确认（双端确认）
- 订单取消
- 订单评价

### 4. 求购管理
- 发布求购信息
- 求购匹配
- 基于求购创建订单

### 5. 即时聊天
- WebSocket 实时通信
- 聊天会话管理
- 消息记录

### 6. AI 功能
- AI 助手对话（通义千问）
- 书籍信息智能补全（Google Books API）
- 书籍描述自动生成

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 配置说明

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/booktrading?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

oss:
  endpoint: oss-cn-hangzhou.aliyuncs.com
  access-key-id: your_access_key_id
  access-key-secret: your_access_key_secret
  bucket-name: your_bucket_name

ai:
  dashscope:
    api-key: your_dashscope_api_key
```

### 启动项目

```bash
# 使用 Maven 编译
mvn clean package

# 运行项目
mvn spring-boot:run

# 或直接运行 JAR
java -jar target/booktrad-0.0.1-SNAPSHOT.jar
```

## API 响应格式

所有 API 统一响应格式：

```json
{
  "code": 1,
  "msg": "操作成功",
  "data": {}
}
```

- `code`: 1 成功，0 失败，其他为业务错误码
- `msg`: 响应消息
- `data`: 响应数据

## 数据库表设计

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| book | 书籍表 |
| book_order | 订单表 |
| order_review | 评价表 |
| wanted_request | 求购表 |
| chat_session | 聊天会话表 |
| chat_message | 聊天消息表 |
| ai_chat_session | AI 会话表 |
| ai_chat_message | AI 消息表 |
| search_history | 搜索历史表 |

## 开发者信息

- **作者**: yaozekai
- **邮箱**: 2321593248@qq.com

## 许可证

Copyright (C) 2025-2026 All Right Reserved
