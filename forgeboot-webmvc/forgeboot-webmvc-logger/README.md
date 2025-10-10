# forgeboot-webmvc-logger

> 📝 请求/响应日志模块，通过 AOP 切面自动记录接口调用日志，支持灵活的日志级别、过滤规则和格式化输出。

---

## 🧩 简介

`forgeboot-webmvc-logger` 是 ForgeBoot WebMVC 系列中的日志记录模块，提供自动化的 HTTP 请求/响应日志记录能力。通过 AOP 切面拦截
Controller 方法，自动记录请求参数、响应结果、执行时间等信息，便于调试和问题排查。

---

## ✨ 核心功能

- ✅ **自动日志记录**：通过注解或全局配置自动记录接口日志
- ✅ **请求信息记录**：记录 HTTP 方法、URL、请求头、请求参数等
- ✅ **响应信息记录**：记录响应状态码、响应体、执行时间等
- ✅ **灵活过滤**：支持按路径、方法、注解等维度过滤日志
- ✅ **敏感信息脱敏**：自动脱敏密码、手机号等敏感字段
- ✅ **性能监控**：记录接口执行耗时，便于性能分析
- ✅ **异常日志**：自动记录接口异常信息和堆栈
- ✅ **自定义格式化**：支持自定义日志输出格式

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-webmvc-logger</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-logger:${version}"
```

---

## 🚀 快速开始

### 1️⃣ 全局启用（推荐）

在 `application.yml` 中配置：

```yaml
forgeboot:
  webmvc:
    logger:
      # 启用日志记录
      enabled: true
      # 日志级别
      level: INFO
      # 是否记录请求头
      log-headers: true
      # 是否记录请求体
      log-request-body: true
      # 是否记录响应体
      log-response-body: true
      # 是否记录执行时间
      log-execution-time: true
```

所有 Controller 的接口都会自动记录日志。

### 2️⃣ 通过注解启用

在特定接口上使用 `@ApiLog` 注解：

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController {
    
    /**
     * 启用日志记录
     */
    @ApiLog
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): User {
        return userService.findById(id)
    }
    
    /**
     * 自定义日志描述
     */
    @ApiLog(description = "创建用户接口")
    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userService.create(user)
    }
    
    /**
     * 禁用日志记录
     */
    @ApiLog(enabled = false)
    @GetMapping("/sensitive")
    fun getSensitiveData(): SensitiveData {
        // 不记录日志
    }
}
```

### 3️⃣ 日志输出示例

**请求日志**：

```
[API-LOG] ===> Request  | POST /api/users
  Headers: {Content-Type=application/json, Authorization=Bearer xxx}
  Body: {"username":"zhangsan","email":"zhangsan@example.com","password":"******"}
  RequestId: a1b2c3d4-e5f6-7890
  Timestamp: 2024-10-08 14:30:00
```

**响应日志**：

```
[API-LOG] <=== Response | POST /api/users | 200 OK | 125ms
  Body: {"id":1,"username":"zhangsan","email":"zhangsan@example.com"}
  RequestId: a1b2c3d4-e5f6-7890
  Timestamp: 2024-10-08 14:30:00
```

**异常日志**：

```
[API-LOG] <=== Error    | POST /api/users | 400 Bad Request | 15ms
  Exception: ValidationException: 用户名已存在
  RequestId: a1b2c3d4-e5f6-7890
  Timestamp: 2024-10-08 14:30:00
```

---

## 🔧 高级配置

### 完整配置示例

```yaml
forgeboot:
  webmvc:
    logger:
      # 是否启用日志记录
      enabled: true
      
      # 日志级别（TRACE, DEBUG, INFO, WARN, ERROR）
      level: INFO
      
      # 是否记录请求头
      log-headers: true
      
      # 是否记录请求体
      log-request-body: true
      
      # 是否记录响应体
      log-response-body: true
      
      # 是否记录执行时间
      log-execution-time: true
      
      # 响应体最大长度（超过则截断）
      max-response-body-length: 1000
      
      # 请求体最大长度（超过则截断）
      max-request-body-length: 1000
      
      # 排除的路径（支持 Ant 风格路径）
      exclude-paths:
        - /actuator/**
        - /health
        - /metrics
        
      # 包含的路径（优先级高于排除）
      include-paths:
        - /api/**
        
      # 敏感字段（自动脱敏）
      sensitive-fields:
        - password
        - token
        - secret
        - phone
        - idCard
        
      # 慢接口阈值（毫秒）
      slow-api-threshold: 1000
```

### 自定义日志格式化

实现自定义日志格式化器：

```kotlin
@Component
class CustomLogFormatter : LogFormatter {
    
    override fun formatRequest(request: HttpServletRequest, body: String?): String {
        return buildString {
            append("【请求】")
            append(request.method)
            append(" ")
            append(request.requestURI)
            if (body != null) {
                append("\n参数: ")
                append(body)
            }
        }
    }
    
    override fun formatResponse(response: Any?, executionTime: Long): String {
        return buildString {
            append("【响应】")
            append("耗时: ${executionTime}ms")
            if (response != null) {
                append("\n结果: ")
                append(response)
            }
        }
    }
}
```

### 敏感信息脱敏

配置敏感字段自动脱敏：

```yaml
forgeboot:
  webmvc:
    logger:
      sensitive-fields:
        - password
        - token
        - secret
        - phone
        - idCard
        - bankCard
```

**脱敏效果**：

```json
// 原始数据
{
  "username": "zhangsan",
  "password": "123456",
  "phone": "13800138000"
}

// 脱敏后
{
  "username": "zhangsan",
  "password": "******",
  "phone": "138****8000"
}
```

### 自定义脱敏策略

```kotlin
@Component
class CustomDataMasker : DataMasker {
    
    override fun mask(fieldName: String, value: String): String {
        return when (fieldName.lowercase()) {
            "password" -> "******"
            "phone" -> maskPhone(value)
            "idcard" -> maskIdCard(value)
            "bankcard" -> maskBankCard(value)
            else -> value
        }
    }
    
    private fun maskPhone(phone: String): String {
        if (phone.length != 11) return phone
        return "${phone.substring(0, 3)}****${phone.substring(7)}"
    }
    
    private fun maskIdCard(idCard: String): String {
        if (idCard.length < 10) return idCard
        return "${idCard.substring(0, 6)}********${idCard.substring(idCard.length - 4)}"
    }
    
    private fun maskBankCard(bankCard: String): String {
        if (bankCard.length < 8) return bankCard
        return "****${bankCard.substring(bankCard.length - 4)}"
    }
}
```

### 慢接口监控

自动标记慢接口：

```yaml
forgeboot:
  webmvc:
    logger:
      # 慢接口阈值：1秒
      slow-api-threshold: 1000
```

**慢接口日志**：

```
[API-LOG] ⚠️ SLOW API | GET /api/users/search | 1523ms
  Threshold: 1000ms
  Exceeded by: 523ms
```

### 集成 MDC（Mapped Diagnostic Context）

自动将 traceId 注入 MDC：

```kotlin
@Component
class MdcLogEnhancer : LogEnhancer {
    
    override fun enhance(request: HttpServletRequest) {
        val traceId = request.getHeader("X-Trace-Id") 
            ?: UUID.randomUUID().toString()
        MDC.put("traceId", traceId)
        MDC.put("clientIp", request.remoteAddr)
        MDC.put("userAgent", request.getHeader("User-Agent"))
    }
    
    override fun clear() {
        MDC.clear()
    }
}
```

**Logback 配置**：

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
</configuration>
```

---

## 📦 模块结构

```
forgeboot-webmvc-logger/
├── forgeboot-webmvc-logger-api/        # API 接口定义
│   ├── annotations/                    # 注解定义
│   │   └── ApiLog.kt                   # @ApiLog 注解
│   └── formatter/                      # 格式化器接口
├── forgeboot-webmvc-logger-impl/       # 默认实现
│   ├── aspect/                         # AOP 切面
│   ├── formatter/                      # 默认格式化器
│   └── masker/                         # 数据脱敏器
└── forgeboot-webmvc-logger-autoconfigure/  # 自动配置
    ├── config/                         # 配置类
    └── properties/                     # 配置属性
```

---

## 🎯 使用场景

| 场景   | 配置建议                     | 说明           |
|------|--------------------------|--------------|
| 开发环境 | `level: DEBUG`, 全量日志     | 便于调试，查看详细信息  |
| 测试环境 | `level: INFO`, 记录请求/响应   | 追踪问题，验证功能    |
| 生产环境 | `level: WARN`, 仅记录异常和慢接口 | 减少日志量，关注核心问题 |
| 性能分析 | 启用 `log-execution-time`  | 识别性能瓶颈       |
| 安全审计 | 记录敏感操作日志                 | 追溯操作记录       |
| 故障排查 | 启用完整日志                   | 快速定位问题       |

---

## 🛡️ 最佳实践

### 1. 生产环境优化

```yaml
forgeboot:
  webmvc:
    logger:
      enabled: true
      level: WARN
      log-request-body: false      # 减少日志量
      log-response-body: false     # 减少日志量
      log-execution-time: true     # 性能监控
      slow-api-threshold: 1000     # 慢接口告警
```

### 2. 排除健康检查接口

```yaml
forgeboot:
  webmvc:
    logger:
      exclude-paths:
        - /actuator/**
        - /health
        - /ping
        - /metrics
```

### 3. 敏感接口禁用日志

```kotlin
@ApiLog(enabled = false)
@PostMapping("/login")
fun login(@RequestBody credentials: LoginRequest): TokenResponse {
    // 不记录敏感登录信息
}
```

### 4. 异步日志记录

配置异步日志提升性能：

```xml
<configuration>
    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="CONSOLE" />
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>
</configuration>
```

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建：

```bash
./gradlew :forgeboot-webmvc:forgeboot-webmvc-logger:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！