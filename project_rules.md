# Sky-Take-Out 项目开发规则

## 1. 项目架构与模块划分

### 1.1 模块划分原则
- 按业务功能模块化，每个功能包内部包含完整的3层架构
- 三层架构：Controller → Service → Mapper
- 高内聚低耦合：相关功能代码集中在同一包下
- 公共组件独立封装，供所有功能包使用
- 禁止跨层调用
- 禁止功能包间直接访问Mapper

### 1.2 包结构规范
以下仅仅只是包结构的示例
com.booktrad
├── common             # 公共组件包
│   ├── constant       # 全局常量
│   ├── context        # 上下文工具
│   ├── exception      # 全局异常类
│   ├── json           # JSON处理
│   ├── properties     # 配置属性
│   ├── result         # 统一结果封装
│   └── utils          # 工具类
├── config             # 配置包
│   ├── WebMvcConfig.java
│   ├── RedisConfig.java
│   ├── JwtConfig.java
│   └── WebSocketConfig.java
├── interceptor        # 拦截器包
│   ├── JwtTokenAdminInterceptor.java
│   └── JwtTokenUserInterceptor.java
├── aspect             # 切面包
│   └── AutoFillAspect.java
├── handler            # 处理器包
│   └── GlobalExceptionHandler.java
├── task               # 定时任务包
│   └── OrderTask.java
├── websocket          # WebSocket包
│   └── WebSocketServer.java
├── admin              # 管理员管理功能包
│   ├── controller     # 控制器层
│   ├── service        # 服务层接口
│   ├── service.impl   # 服务层实现
│   ├── mapper         # 数据访问层
│   ├── dto            # 数据传输对象
│   ├── entity         # 数据库实体
│   └── vo             # 视图对象
├── user               # 用户管理功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── book               # 书籍管理功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── order              # 订单管理功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── category           # 书籍分类功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── shoppingcart       # 购物车功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── addressbook        # 地址簿功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
├── comment            # 评论管理功能包
│   ├── controller
│   ├── service
│   ├── service.impl
│   ├── mapper
│   ├── dto
│   ├── entity
│   └── vo
└── favorite           # 收藏管理功能包
    ├── controller
    ├── service
    ├── service.impl
    ├── mapper
    ├── dto
    ├── entity
    └── vo
```

### 1.3 功能包命名规范
- 按业务功能命名，使用小写单数形式
- 示例：admin（管理员管理）、user（用户管理）、book（书籍管理）、order（订单管理）
- 禁止使用缩写或拼音

### 1.4 功能包内部结构要求
- 每个功能包必须包含完整的三层架构
- 功能包内可以包含自己的常量、枚举、异常等
- 优先使用公共包的组件，避免重复开发
- 功能包间通过Service接口进行调用，禁止直接访问Mapper

### 1.5 公共包使用原则
- 公共包只包含所有功能包共享的组件
- 公共包禁止依赖任何功能包
- 公共组件必须经过充分测试，确保稳定性
- 公共组件的修改必须经过严格审查

## 2. 代码规范

### 2.1 Java代码规范
- 遵循阿里巴巴Java开发手册
- Lombok：@Data（实体/DTO/VO），@Slf4j（服务类）
- 方法参数≤5个，超则用DTO
- 访问权限最小化
- 禁止魔法值
- 集合初始化指定容量
- 空指针检查：Optional优先
- 资源关闭：try-with-resources

### 2.2 代码风格
- 缩进：4空格
- 行宽：≤120字符
- 大括号：换行风格
- 空行：类成员与方法、方法间、逻辑块间空一行
- 变量：局部变量用前声明，成员变量在类顶
- 空格：二元运算符两侧、逗号/分号后有空格

### 2.3 注释规范
- 语言：中文
- 类注释：Javadoc（功能、作者、创建时间）
- 方法注释：Javadoc（功能、参数、返回值、异常）
- 代码注释：复杂逻辑、业务含义不明确处加注释
- 注释与代码同步

### 2.4 IDE配置
- 强制使用：IntelliJ IDEA
- 代码格式化：.editorconfig
- 自动导入：开启
- 代码检查：开启

## 3. 命名规则

### 3.1 包命名
- 格式：全小写，点分隔，反向域名+项目+功能
- 示例：com.sky.controller.admin
- 禁止：拼音/缩写/单个字母

### 3.2 类命名
- 大驼峰
- 后缀规范：Controller/Service/ServiceImpl/Mapper/DTO/VO/Exception/Enum/Config/Util
- 示例：EmployeeController、EmployeeServiceImpl

### 3.3 方法命名
- 小驼峰，动词+名词
- 前缀规范：
  - 查询：get/find/list/query/page
  - 保存：save/insert
  - 更新：update/modify
  - 删除：delete/remove/batchDelete
  - 启用：enable/disable
  - 统计：count/sum
  - 登录：login/logout
  - 校验：validate/check

### 3.4 变量命名
- 小驼峰，见名知意
- 禁止：拼音/单个字母（循环变量除外）
- 布尔：is/has前缀
- 集合/数组：复数形式

### 3.5 常量与枚举
- 常量：全大写，下划线分隔
- 枚举：类名大驼峰+Enum，值全大写

### 3.6 配置文件命名
- Spring Boot：application-环境.yml
- MyBatis：Mapper接口同名.xml
- 日志：logback-spring.xml

### 3.7 其他命名
- 数据库表/字段：全小写，下划线分隔
- Redis键：冒号分隔
- WebSocket消息：大驼峰/全大写

## 4. 数据库设计规范

### 4.1 表命名
- 全小写，下划线分隔，见名知意
- 关联表：两表名下划线分隔
- 禁止：拼音/关键字

### 4.2 字段命名
- 全小写，下划线分隔
- 主键：id（BIGINT，自增）
- 通用字段：create_time/update_time/create_user/update_user
- 状态：status（TINYINT，0禁用1启用）
- 时间：DATETIME
- 金额：DECIMAL(10,2)

### 4.3 索引规范
- 命名：uk_字段名（唯一），idx_字段名（普通），idx_字段1_字段2（复合）
- 原则：查询条件、外键、ORDER BY/GROUP BY字段建索引
- 单个表索引≤6个
- 避免频繁更新字段/低选择性字段建索引

### 4.4 SQL规范
- 编写位置：MyBatis XML
- SELECT：禁止*，明确字段
- WHERE：禁止索引字段函数操作，禁止!=，禁止OR
- JOIN：≤3表，优先INNER JOIN
- 批量操作：foreach，≤1000条
- 分页：LIMIT，禁止OFFSET大分页，必须ORDER BY

### 4.5 注释规范
- 表/字段必须有COMMENT
- 复杂SQL加注释

## 5. 安全规范

### 5.1 认证与授权
- JWT认证，区分admin/user密钥
- 拦截器：JwtTokenAdminInterceptor/JwtTokenUserInterceptor
- 敏感操作权限校验
- Token：2小时过期，含用户ID/角色，无敏感信息

### 5.2 数据安全
- 密码：BCrypt/Argon2加密，加盐
- 敏感数据脱敏：手机号138****1234
- 日志：无敏感信息，生产环境无DEBUG

### 5.3 接口安全
- 参数校验：Spring Validation
- 防止注入：#{}占位符，HTML转义
- 接口限流：Redis/Guava RateLimiter
- 文件上传：限制类型/大小，安全位置，重命名

### 5.4 其他安全
- 依赖：定期检查漏洞，更新
- 生产环境：关闭Swagger/调试模式，HTTPS
- 数据库：强密码，非root用户，SSL加密

## 6. API设计规范

### 6.1 RESTful API设计
- HTTP方法：GET查询，POST创建，PUT更新，DELETE删除，PATCH部分更新
- 资源：名词复数，小写，连字符分隔
- 路径参数：{id}，必须校验
- 查询参数：过滤、排序、字段选择

### 6.2 接口版本控制
- URL中加版本号：v1
- 不兼容变更升版本，旧版本保留≥6个月

### 6.3 响应格式规范
```json
{
  "code": 1,        // 1成功，0失败，其他业务错误码
  "msg": "操作成功", // 响应消息
  "data": {}        // 响应数据
}
```

### 6.4 分页查询规范
- 请求：page（默认1），pageSize（默认10，最大100）
- 响应：total+records+page+pageSize

### 6.5 API文档规范
- Swagger/knife4j，开发环境/doc.html
- 生产环境关闭

### 6.6 接口命名规范
- 后台：/admin前缀
- 用户端：/user前缀
- 公共：/api前缀
- WebSocket：/ws前缀

## 7. 异常处理规范

### 7.1 异常分类
- 业务异常：BaseException子类，可预见业务错误
- 系统异常：Spring/Java内置异常，不可预见错误
- 参数校验异常：Validation框架异常

### 7.2 异常处理
- @ControllerAdvice+@ExceptionHandler统一处理
- 异常日志：INFO（业务），ERROR（系统，含堆栈），WARN（参数）
- 响应：统一Result格式

### 7.3 异常抛出原则
- 明确类型，信息具体
- 不吞噬异常，至少记日志
- 不滥用异常，用于控制流程
- 无法处理则向上传递

### 7.4 自定义异常规范
- 命名：Exception结尾
- 继承：BaseException
- 构造方法：无参、带消息、带消息+cause

## 8. 日志规范

### 8.1 日志框架
- SLF4J+Logback
- 禁止直接使用Log4j/Log4j2/JUL
- @Slf4j注解

### 8.2 日志使用规范
- 格式：log.info("描述：{}", 参数)，禁止字符串拼接
- 内容：清晰，含上下文，无敏感信息
- 循环：每1000条打一次日志
- 请求日志：方法+URL+参数+状态+响应时间

### 8.3 日志级别规范
- DEBUG：开发环境调试，生产关闭
- INFO：正常业务流程
- WARN：警告（参数校验失败等）
- ERROR：错误（系统异常等，含堆栈）

### 8.4 日志配置规范
- 配置文件：logback-spring.xml
- 输出：控制台（开发）+文件（所有环境）
- 文件：application-环境-年月日.log，按天滚动，保留7天，≤100MB
- 脱敏：敏感信息处理

## 9. 测试规范

### 9.1 测试类型
- 单元测试：单个方法/类，隔离，快
- 集成测试：多模块交互，依赖外部资源
- 接口测试：API正确性，HTTP模拟
- 端到端测试：全流程测试

### 9.2 测试覆盖
- 核心业务≥80%，工具类/服务层=100%，控制器≥80%
- 覆盖：正常/异常/边界/并发场景

### 9.3 测试框架与工具
- 单元：JUnit 5+Mockito
- 集成：Spring Boot Test
- 接口：Postman/Newman/REST Assured
- 报告：Jacoco+Allure
- CI/CD：Jenkins+Maven/Gradle

### 9.4 测试规范
- 测试类：被测试类名+Test
- 测试方法：test方法名_场景_预期结果
- 数据：随机，与代码分离，测试后清理
- Mock：外部依赖模拟
- 断言：JUnit 5 Assertions

### 9.5 测试执行
- 开发：提交前运行单元测试
- 集成：合并前运行集成测试
- 发布：运行所有测试，覆盖率达标

## 10. 部署规范

### 10.1 环境分离
- 环境：DEV（开发）、TEST（测试）、UAT（预发布）、PROD（生产）
- 隔离：物理/网络隔离，中间件分离

### 10.2 配置分离
- 多环境配置：application-环境.yml
- 敏感配置：环境变量/配置中心/加密文件
- 优先级：命令行>环境变量>配置文件>默认值

### 10.3 部署方式
- 容器化：Docker+Dockerfile+docker-compose
- 编排：Docker Compose/K8s
- CI/CD流水线：代码提交→审查→测试→构建→部署→验证

### 10.4 部署流程
- 部署前：文档，备份，通知
- 部署中：低峰期，蓝绿/滚动部署，监控
- 部署后：检查服务/日志，功能测试，监控
- 回滚：详细方案，简单快速

### 10.5 监控与告警
- 指标：系统（CPU/内存）、应用（请求/响应/错误）、业务（订单/金额）
- 工具：Prometheus+Grafana（系统），Spring Boot Actuator+Micrometer（应用），ELK（日志）
- 告警：CPU/内存>80%，错误率>5%，服务不可用
- 渠道：邮件/短信/即时通讯/电话

## 11. Git规范
- 分支：功能分支feature/名称
- 提交信息：清晰简洁，格式：类型: 描述
- 定期合并主分支

## 12. 性能优化规范
- 缓存：合理使用，减少DB访问
- 避免N+1查询
- 大文件：分片上传
- 图片：CDN加速

## 13. 技术栈最佳实践
- 核心技术栈：Spring Boot 3.2.0，MyBatis Plus 3.5.7，MySQL 8.0，Redis 6.0，JWT 0.9.1，Lombok 1.18.20
- 依赖管理：
  - 父pom统一管理所有依赖版本
  - 功能包间禁止循环依赖
  - 禁止引入不必要的依赖
  - 定期更新依赖版本，修复安全漏洞
  - 功能包内依赖：
    - controller层：仅依赖service层和common包
    - service层：仅依赖mapper层和common包
    - mapper层：仅依赖entity和common包
    - dto/entity/vo：仅依赖common包
- 技术栈使用原则：
  - 严格遵循各技术栈的最佳实践
  - 禁止混合使用相似功能的技术栈
  - 新技术栈引入前必须经过评估和测试

## 14. 代码审查规范
- 必须进行代码审查
- 关注：代码质量、安全性、性能、可读性