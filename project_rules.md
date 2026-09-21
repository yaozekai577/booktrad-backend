# BookTrad 开发规范

> 校园二手书交易平台 · 后端开发规范
> 适用模块：`com.booktrad` 下全部功能包
> 维护人：yaozekai

---

## 1. 项目概览

### 1.1 定位

面向高校场景的二手书籍交易平台后端，覆盖「发布 → 求购匹配 → 下单 → 双方确认交易 → 评价」的完整闭环，并提供即时聊天与 AI 助手两类增强能力。

### 1.2 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis Plus | 3.5.7 | ORM 框架（显式引入 mybatis-spring 3.0.3） |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存与会话 |
| jjwt | 0.12.5 | JWT 签发与校验 |
| spring-security-crypto | — | 密码 BCrypt 加密 |
| spring-boot-starter-websocket | — | 即时聊天长连接 |
| 阿里云 OSS SDK | 3.17.1 | 书籍封面图存储 |
| dashscope-sdk-java | 2.12.0 | 通义千问大模型调用 |
| OkHttp | 4.12.0 | 调用 Google Books API |
| Guava | 32.1.3-jre | dashscope-sdk 传递依赖，版本锁定 |
| Lombok | — | 样板代码简化 |

### 1.3 数据表

| 表名 | 说明 |
|------|------|
| `user` | 用户 |
| `book` | 书籍（含软删除 `deleted_at`、封禁标记 `is_banned`） |
| `book_category` | 书籍分类 |
| `book_favorite` | 收藏 |
| `book_order` | 订单 |
| `order_review` | 订单评价 |
| `wanted_request` | 求购信息 |
| `chat_session` / `chat_message` | 聊天会话与消息 |
| `ai_chat_session` / `ai_chat_message` | AI 助手会话与消息 |
| `search_history` | 搜索历史 |

---

## 2. 包结构

### 2.1 顶层划分

```
com.booktrad
├── BooktradApplication          # 启动类
├── ai/                          # AI 助手、自然语言检索、图书信息补全
├── book/                        # 书籍管理、搜索历史、文件上传
├── chat/                        # 即时聊天（含 websocket 子包）
├── common/                      # 公共组件
│   ├── context                  # UserContext：ThreadLocal 持有当前用户
│   ├── result                   # Result：统一响应封装
│   └── utils                    # JwtUtil 等工具类
├── config/                      # 全局配置
├── favorite/                    # 收藏
├── interceptor/                 # JwtLoginInterceptor
├── order/                       # 订单与评价
├── user/                        # 用户
└── wanted/                      # 求购
```

### 2.2 功能包内部结构

除 `favorite`（无 dto，入参直接使用实体）外，每个功能包统一为：

```
{module}
├── controller        # 仅做参数接收与结果封装
├── service           # 接口
├── service/impl      # 实现，业务逻辑与事务边界所在
├── mapper            # 数据访问接口（继承 MyBatis Plus BaseMapper）
├── dto               # 入参对象
├── entity            # 数据库实体
└── vo                # 出参对象
```

`chat` 额外包含 `websocket` 子包，存放 `ChatWebSocketHandler` 与 `WebSocketEvent`。

### 2.3 全局配置类

| 类 | 职责 |
|---|---|
| `WebMvcConfig` | CORS、拦截器注册、`/upload/**` 静态资源映射 |
| `WebSocketConfig` | 注册 `/ws/chat` 端点 |
| `OssConfig` | 初始化 OSS 客户端 Bean |
| `MyMetaObjectHandler` | MyBatis Plus 自动填充 `createdAt` / `updatedAt` |
| `CryptoConfig` | 暴露 BCrypt 编码器 Bean |

### 2.4 依赖方向约束

```
controller ──▶ service ──▶ mapper ──▶ entity
                 │
                 └──▶ common（result / context / utils）
```

- 禁止 controller 直接调用 mapper
- 禁止跨功能包直接访问 mapper，跨模块取数一律走对方的 service
- `common` 不得依赖任何功能包
- `entity` / `dto` / `vo` 只允许依赖 `common` 与第三方库

---

## 3. 命名规范

### 3.1 Java

| 对象 | 规则 | 示例 |
|---|---|---|
| 包 | 全小写，业务单词单数 | `com.booktrad.wanted` |
| 类 | 大驼峰 + 类型后缀 | `WantedRequestController` |
| 方法 | 小驼峰，动词前缀 | `pageBooks`、`publishBook`、`confirmTrade` |
| 变量 | 小驼峰，见名知意 | `sellerId`、`bookCondition` |
| 常量 | 全大写下划线 | `MAX_UPLOAD_SIZE` |
| 布尔 | `is` / `has` 前缀 | `isBanned` |

类型后缀固定为：`Controller` / `Service` / `ServiceImpl` / `Mapper` / `DTO` / `VO` / `Config` / `Util` / `Handler` / `Interceptor`。

禁止拼音命名、单字母命名（循环变量除外）、无意义缩写。

### 3.2 数据库

- 表名、字段名：全小写下划线，见名知意，禁止拼音与保留字
- 主键统一 `id`，`BIGINT` 自增
- 金额统一 `DECIMAL(10,2)`，禁止浮点类型
- 状态类字段统一 `TINYINT`
- 索引命名：`idx_字段名`、`uk_字段名`、`idx_字段1_字段2`

> 时间字段现状：主流表使用 `created_at` / `updated_at`（与 `MyMetaObjectHandler` 的填充字段一致），少数早期表使用 `create_time` / `update_time`。**新表一律用 `created_at` / `updated_at`**，历史表在后续迭代中逐步统一。

---

## 4. 接口规范

### 4.1 路径前缀

| 前缀 | 用途 |
|---|---|
| `/api/auth/**` | 登录、注册、卖家信息 |
| `/api/book/**`、`/api/order/**`、`/api/wanted/**`、`/api/favorite/**`、`/api/review/**`、`/api/ai/**`、`/api/upload/**` | 各业务模块 |
| `/ws/chat` | WebSocket 聊天端点 |
| `/upload/**` | 上传文件的静态访问路径 |

路径使用名词、小写、连字符分隔。版本号不引入（当前为个人项目单版本）。

### 4.2 统一响应

所有接口返回 `com.booktrad.common.result.Result<T>`：

```json
{
  "code": 1,
  "msg": "操作成功",
  "data": {}
}
```

- `code = 1`：成功
- `code = 0`：业务失败
- `code = 401`：登录失效，前端拦截器统一处理跳转登录页

新增接口一律通过 `Result.success(...)` / `Result.error(...)` 构造，**禁止**直接返回裸对象或自定义 Map。

### 4.3 分页

统一使用 MyBatis Plus 的 `IPage`：

- 入参：`pageNum`（默认 1）、`pageSize`（默认 10，上限 100）
- 出参：`IPage<XxxVO>`，配合各模块的 `XxxPageVO`
- 禁止手写 `LIMIT offset` 实现分页

### 4.4 DTO / VO 使用

- 入参一律用 `DTO`，**禁止**直接用 `entity` 接收请求体
- 出参一律用 `VO`，**禁止**直接把 `entity` 返回给前端（避免 `password` 等敏感字段外泄）

---

## 5. 认证与安全

### 5.1 认证流程

1. 登录成功后由 `JwtUtil` 签发 JWT，前端存入 `localStorage`
2. 前端以 `Authorization: Bearer {token}` 携带
3. `JwtLoginInterceptor` 拦截 `/**` 并解析 token，写入 `UserContext`
4. 业务层通过 `UserContext.getUserId()` 获取当前用户

**放行清单**（`WebMvcConfig.addInterceptors` 中维护）：

```
/api/auth/login
/api/auth/register
/api/auth/seller/**
/api/wanted/page
```

新增免登录接口必须同步更新该清单，并在 code review 时说明放行理由。

`UserContext` 使用 `ThreadLocal` 存储，**必须在请求结束时调用 `clear()`**，否则线程池复用会导致用户身份串号与内存泄漏。

### 5.2 密码与敏感数据

- 密码一律 BCrypt 加密存储，禁止明文或 MD5
- 任何接口不得返回 `password` 字段
- 日志中禁止打印密码、token、手机号完整值

### 5.3 配置与密钥

**这条是硬性要求：**

- 数据库密码、Redis 密码、OSS AccessKey、dashscope API Key 等一律通过**环境变量**注入，`application.yaml` 中只允许出现占位符：

```yaml
oss:
  access-key-id: ${OSS_ACCESS_KEY_ID:}
  access-key-secret: ${OSS_ACCESS_KEY_SECRET:}
```

- `.env` 与任何含真实凭据的本地配置文件**必须**写入 `.gitignore`
- 一旦密钥被提交进 Git 历史，即使后续删除也要视为已泄露，**必须到云厂商控制台轮换**

### 5.4 文件上传

- 限制扩展名白名单与单文件大小（当前上限 10MB，见 `spring.servlet.multipart`）
- 文件名重命名，禁止使用用户原始文件名落盘
- 上传目录必须位于 jar 包外部（`application.yaml` 的 `file.upload.path` 可配），否则打包后无法写入

---

## 6. 数据库与 SQL

- SQL 写在 `resources/mapper/**/*.xml`，简单单表查询优先走 MyBatis Plus 的 `Wrapper`
- `SELECT` 必须显式列出字段，禁止 `SELECT *`
- `WHERE` 条件字段上禁止套函数，禁止对索引列做隐式类型转换
- `JOIN` 不超过 3 张表，优先 `INNER JOIN`
- 批量写入使用 `foreach`，单批不超过 1000 条
- 软删除表（如 `book.deleted_at`）查询必须带 `deleted_at IS NULL`
- 所有表、字段必须写 `COMMENT`

---

## 7. 异常处理

- 业务异常统一抛自定义异常，由全局异常处理器转成 `Result.error(...)`
- 禁止 `catch` 后不处理、不打日志
- 禁止用异常控制正常业务流程
- 对外响应不暴露堆栈信息，堆栈只进日志

---

## 8. 日志

- 统一使用 Lombok 的 `@Slf4j`，禁止 `System.out.println`
- 用占位符风格：`log.info("发布书籍成功，bookId={}", bookId)`
- 级别约定：`INFO` 正常流程 / `WARN` 参数校验失败 / `ERROR` 系统异常（带堆栈）
- 循环体内每 1000 条打印一次，禁止逐条打

---

## 9. WebSocket 规范

- 端点：`/ws/chat`，通过 URL 参数携带 token 完成鉴权
- 消息体统一使用 `WebSocketEvent` 封装，包含事件类型与负载
- 前端在 HTTPS 环境下必须使用 `wss://`，否则浏览器会拦截明文连接
- Nginx 反向代理必须配置 `Upgrade` 与 `Connection: upgrade` 头，并延长 `proxy_read_timeout`

---

## 10. Git 规范

### 10.1 提交信息

格式：`类型: 描述`

| 类型 | 含义 |
|---|---|
| `feat` | 新功能 |
| `fix` | 修复缺陷 |
| `refactor` | 重构，不改变外部行为 |
| `docs` | 文档 |
| `chore` | 构建、依赖、配置 |

示例：`feat: 求购广场支持按分类筛选`、`fix: 修复图片回显路径错误`

**禁止**使用「调试」「瞎折腾」「更新」这类无信息量的描述。

### 10.2 禁止提交的内容

```
target/
node_modules/
dist/
.idea/
*.iml
.env
*.log
```

---

## 11. 代码风格

- 缩进 4 空格，行宽不超过 120 字符
- Lombok 使用约定：实体/DTO/VO 用 `@Data`，服务实现类用 `@Slf4j`
- 方法参数不超过 5 个，超出封装为 DTO
- 集合初始化指定容量
- 资源关闭使用 try-with-resources
- 所有类、公开方法写 Javadoc，使用中文

类注释模板：

```java
/**
 * 项目名称：booktrad
 * 版本：V1.0
 *
 * @Author yaozekai
 * @Email 2321593248@qq.com
 * @Description 描述本类职责
 * @Date yyyy-MM-dd HH:mm
 */
```
