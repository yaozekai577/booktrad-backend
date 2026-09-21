# BookTrad · 校园二手书交易平台（后端）

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)
![MyBatis Plus](https://img.shields.io/badge/MyBatis_Plus-3.5.7-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-6.0+-DC382D?logo=redis&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-实时通信-010101?logo=socketdotio&logoColor=white)

> **前端仓库**：[yaozekai577/booktrad-frontend](https://github.com/yaozekai577/booktrad-frontend)

## 项目简介

BookTrad 是一个面向高校场景的二手书交易平台，解决校园内教材与课外书流转效率低、信息分散的问题。用户可以发布闲置书籍、发布求购需求，系统基于求购信息做双向匹配；买卖双方通过站内实时聊天沟通细节，订单采用**双端确认机制**完成交易，交易结束后可互相评价，形成完整的信用闭环。

平台的核心差异点在于 **AI 能力的深度集成**：接入通义千问大模型实现 AI 助手对话（流式响应输出）与自然语言查书——用户可以直接说「帮我找一本三十块以内、九成新的算法书」，系统自动解析意图并返回结果；同时调用 Google Books API 支持按 ISBN 自动补全书名、作者、出版社与封面，发布一本书只需输入 ISBN 就能完成大部分信息填写；此外还提供 AI 智能生成书籍描述，降低发布门槛。

代码按业务模块化组织，每个模块内部遵循 `Controller → Service → Mapper` 的分层结构，模块间通过 Service 接口调用，禁止跨包直接访问 Mapper。

## 核心亮点

| 能力 | 说明 |
|------|------|
| **AI 助手对话** | 基于通义千问，支持流式响应（SSE）输出与多轮上下文，会话与消息持久化 |
| **自然语言查书** | 将口语化查询解析为结构化检索条件，无需用户理解分类和筛选器 |
| **ISBN 一键补全** | 调用 Google Books API，输入 ISBN 自动填充书名、作者、出版社、封面 |
| **AI 生成书籍描述** | 根据书名与成色生成描述草稿，减少发布时的填写成本 |
| **订单双端确认** | 下单后需买卖双方分别确认才流转状态，避免单方面成交 |
| **求购双向匹配** | 卖家可基于求购信息直接发起交易，反向撮合 |
| **站内实时聊天** | 基于 WebSocket 长连接，支持会话列表、消息落库与图片消息 |
| **管理端数据看板** | 提供书籍、订单、用户的统计接口，供前端 ECharts 可视化 |

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis Plus | 3.5.7 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存与会话 |
| jjwt | 0.12.5 | JWT 签发与校验 |
| spring-security-crypto | — | 密码 BCrypt 加密 |
| spring-boot-starter-websocket | — | 实时聊天长连接 |
| 阿里云 OSS SDK | 3.17.1 | 书籍封面图存储 |
| dashscope-sdk-java | 2.12.0 | 通义千问大模型调用 |
| OkHttp | 4.12.0 | 调用 Google Books API |
| Lombok | — | 样板代码简化 |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 1. 初始化数据库

```bash
mysql -uroot -p < booktrading.sql
```

脚本会创建 `booktrading` 库及全部 12 张业务表（含建表注释与索引）。

### 2. 配置环境变量

所有敏感配置均通过**环境变量**注入，`application.yaml` 中不含任何明文凭据。

**IDEA**：`Run → Edit Configurations → Environment variables`

**命令行**：

```bash
export MYSQL_PASSWORD=your_password
export REDIS_PASSWORD=your_redis_password
export OSS_ACCESS_KEY_ID=your_access_key_id
export OSS_ACCESS_KEY_SECRET=your_access_key_secret
export OSS_BUCKET_NAME=your_bucket_name
export DASHSCOPE_API_KEY=your_dashscope_api_key
```

### 3. 编译与启动

```bash
# 编译打包
./mvnw -B -DskipTests package

# 运行
./mvnw spring-boot:run
# 或
java -jar target/booktrad-0.0.1-SNAPSHOT.jar
```

服务默认监听 **8080** 端口。

> **Windows 用户注意**：`mvn` / `mvnw` 在 **Git Bash 下会失败**，报
> `找不到或无法加载主类 org.codehaus.plexus.classworlds.launcher.Launcher`。
> 原因是 Git Bash 把路径转成 Unix 风格后传给原生 Windows 的 java，java 解析不了。
> 请改用 **PowerShell 或 cmd** 执行，并确认 `JAVA_HOME` 指向 JDK 17：
>
> ```powershell
> $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
> .\mvnw.cmd -B -DskipTests package
> ```

## 配置说明

| 环境变量 | 必填 | 默认值 | 说明 |
|----------|:----:|--------|------|
| `MYSQL_URL` | 否 | `jdbc:mysql://localhost:3306/booktrading?...` | 数据库连接串 |
| `MYSQL_USERNAME` | 否 | `root` | 数据库用户名 |
| `MYSQL_PASSWORD` | **是** | — | 数据库密码 |
| `REDIS_HOST` | 否 | `localhost` | Redis 地址 |
| `REDIS_PASSWORD` | **是** | — | Redis 密码 |
| `FILE_UPLOAD_PATH` | 否 | `./data/upload` | 本地文件上传目录 |
| `OSS_ENDPOINT` | OSS 功能 | `oss-cn-hangzhou.aliyuncs.com` | OSS 服务端点 |
| `OSS_ACCESS_KEY_ID` | OSS 功能 | `not-configured` | 阿里云 AccessKeyId |
| `OSS_ACCESS_KEY_SECRET` | OSS 功能 | `not-configured` | 阿里云 AccessKeySecret |
| `OSS_BUCKET_NAME` | OSS 功能 | — | OSS 存储空间名称 |
| `OSS_REGION` | 否 | `oss-cn-hangzhou` | OSS 所在区域 |
| `DASHSCOPE_API_KEY` | AI 功能 | — | 通义千问 API Key |
| `GOOGLE_BOOKS_API_KEY` | 否 | — | 可选，配置后可提高 Google Books 调用限额 |

> **注意**：`OSS_ACCESS_KEY_ID` / `OSS_ACCESS_KEY_SECRET` 的默认值**不可留空**。
> OSS 客户端在应用启动时构建，空字符串会抛 `InvalidCredentialsException`
> 导致整个应用启动失败。未配置时使用 `not-configured` 占位值，
> 应用可正常启动，仅在真正调用 OSS 上报时返回鉴权错误。

## 接口约定

### 路径前缀

| 前缀 | 模块 |
|------|------|
| `/api/auth/**` | 登录、注册、卖家信息 |
| `/api/book/**` | 书籍发布、检索、上下架 |
| `/api/order/**` | 订单与交易确认 |
| `/api/review/**` | 订单评价 |
| `/api/wanted/**` | 求购广场 |
| `/api/favorite/**` | 收藏 |
| `/api/ai/**` | AI 助手与智能查书 |
| `/api/upload/**` | 文件上传 |
| `/ws/chat` | WebSocket 聊天端点 |
| `/upload/**` | 上传文件静态访问映射 |

### 统一响应格式

```json
{
  "code": 1,
  "msg": "操作成功",
  "data": {}
}
```

| code | 含义 |
|:----:|------|
| `1` | 成功 |
| `0` | 业务失败 |
| `401` | 登录已失效，前端拦截器统一跳转登录页 |

分页接口统一使用 MyBatis Plus 的 `IPage`，入参为 `pageNum`（默认 1）、`pageSize`（默认 10，上限 100）。

### 认证方式

登录成功后由服务端签发 JWT，客户端以 `Authorization: Bearer {token}` 携带。`JwtLoginInterceptor` 拦截全部请求并解析 token 写入 `UserContext`，业务层通过 `UserContext.getUserId()` 获取当前用户。

**免登录白名单**（维护在 `WebMvcConfig#addInterceptors`）：

```
/api/auth/login
/api/auth/register
/api/auth/seller/**
/api/wanted/page
```

新增免登录接口必须同步更新该清单。

### WebSocket

- 端点：`/ws/chat`，通过 URL 参数携带 token 完成鉴权
- 消息体统一由 `WebSocketEvent` 封装，包含事件类型与负载
- 生产环境（HTTPS）下客户端必须使用 `wss://`，否则浏览器会拦截明文连接
- 经 Nginx 反向代理时需配置 `Upgrade` 与 `Connection: upgrade` 头并延长读超时

## 项目结构

```
src/main/java/com/booktrad
├── BooktradApplication.java          # 启动类
├── ai                                # AI 助手、自然语言检索、图书信息补全
│   ├── config / controller / dto / entity / mapper / vo
│   └── service (+ impl)
├── book                              # 书籍管理、搜索历史、文件上传
│   ├── controller / dto / entity / mapper / vo
│   └── service (+ impl)
├── chat                              # 即时聊天
│   ├── controller / dto / entity / mapper / vo
│   ├── service (+ impl)
│   └── websocket                     # ChatWebSocketHandler、WebSocketEvent
├── common                            # 公共组件
│   ├── context                       # UserContext：ThreadLocal 持有当前用户
│   ├── result                        # Result：统一响应封装
│   └── utils                         # JwtUtil 等工具类
├── config                            # 全局配置
│   ├── WebMvcConfig                  # CORS、拦截器注册、静态资源映射
│   ├── WebSocketConfig               # 注册 /ws/chat 端点
│   ├── OssConfig                     # OSS 客户端 Bean
│   ├── MyMetaObjectHandler           # createdAt / updatedAt 自动填充
│   └── CryptoConfig                  # BCrypt 编码器 Bean
├── favorite                          # 收藏
├── interceptor                       # JwtLoginInterceptor
├── order                             # 订单与评价
├── user                              # 用户
└── wanted                            # 求购

src/main/resources
├── application.yaml
└── mapper                            # 10 个 MyBatis XML 映射文件
```

依赖方向约束：`controller → service → mapper → entity`，`common` 不依赖任何业务模块，禁止跨模块直接访问 Mapper。

## 数据库设计

| 表名 | 说明 |
|------|------|
| `user` | 用户 |
| `book` | 书籍（含软删除 `deleted_at`、封禁标记 `is_banned`） |
| `book_category` | 书籍分类 |
| `book_favorite` | 收藏 |
| `book_order` | 订单 |
| `order_review` | 订单评价 |
| `wanted_request` | 求购信息 |
| `chat_session` | 聊天会话 |
| `chat_message` | 聊天消息 |
| `ai_chat_session` | AI 会话 |
| `ai_chat_message` | AI 消息 |
| `search_history` | 搜索历史 |

完整建表语句见 [`booktrading.sql`](./booktrading.sql)。

## 界面预览

<!--
截图待补充。把图片放入 docs/images/ 后，删除本注释包裹即可自动显示。

| 首页书城 | 书籍详情 |
|:--------:|:--------:|
| ![首页](docs/images/home.png) | ![详情](docs/images/book-detail.png) |

| AI 助手 | 实时聊天 |
|:-------:|:--------:|
| ![AI助手](docs/images/ai-assistant.png) | ![聊天](docs/images/chat.png) |
-->

## 开发规范

包结构、命名规则、分层约束、数据库与接口规范、Git 提交约定等详见 [`project_rules.md`](./project_rules.md)。

## 作者

**yaozekai** · 2321593248@qq.com
