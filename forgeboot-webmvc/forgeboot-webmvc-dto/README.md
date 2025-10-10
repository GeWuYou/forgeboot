# forgeboot-webmvc-dto

> 📦 统一响应封装模块，提供标准化的 API 响应结构、分页结果封装以及灵活的响应构建器。

---

## 🧩 简介

`forgeboot-webmvc-dto` 是 ForgeBoot WebMVC 系列中的响应封装模块，提供统一的 API
响应格式，支持成功/失败响应、分页结果、自定义扩展字段等功能。通过标准化的响应结构，提升前后端协作效率。

---

## ✨ 核心功能

- ✅ **统一响应结构**：标准化的 API 响应格式（成功/失败）
- ✅ **分页结果封装**：内置分页数据结构 [
  `PageResult`](forgeboot-webmvc-dto-api/src/main/kotlin/com/gewuyou/forgeboot/webmvc/dto/api/entities/PageResult.kt:31)
- ✅ **响应构建器**：流式 API 快速构建响应
- ✅ **国际化支持**：集成 InfoResolver 支持多语言响应消息
- ✅ **请求 ID 注入**：自动注入 traceId/requestId 便于追踪
- ✅ **扩展字段支持**：通过 [
  `ExtraContributor`](forgeboot-webmvc-dto-api/src/main/kotlin/com/gewuyou/forgeboot/webmvc/dto/api/entities/ExtraContributor.kt:29)
  动态添加响应字段
- ✅ **灵活配置**：支持自定义响应码、消息模板等

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-webmvc-dto</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-dto:${version}"
```

---

## 🚀 快速开始

### 1️⃣ 基础使用

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val apiResponses: ApiResponses) {
    
    /**
     * 成功响应 - 返回数据
     */
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ApiResponse {
        val user = userService.findById(id)
        return apiResponses.success(user)
    }
    
    /**
     * 成功响应 - 仅消息
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ApiResponse {
        userService.delete(id)
        return apiResponses.successMessage("用户删除成功")
    }
    
    /**
     * 失败响应
     */
    @PostMapping
    fun createUser(@RequestBody user: User): ApiResponse {
        return try {
            val created = userService.create(user)
            apiResponses.success(created)
        } catch (e: ValidationException) {
            apiResponses.failure(400, "参数校验失败: ${e.message}")
        }
    }
}
```

### 2️⃣ 响应结构

**成功响应（带数据）**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "张三",
    "email": "zhangsan@example.com"
  },
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

**成功响应（仅消息）**：

```json
{
  "code": 200,
  "message": "用户删除成功",
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

**失败响应**：

```json
{
  "code": 400,
  "message": "参数校验失败: 用户名不能为空",
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

### 3️⃣ 分页结果

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val apiResponses: ApiResponses) {
    
    @GetMapping
    fun listUsers(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse {
        val users = userService.findAll(page, size)
        
        val pageResult = PageResult<User>().apply {
            this.data = users.content
            this.total = users.totalElements
            this.page = page
            this.size = size
        }
        
        return apiResponses.success(pageResult)
    }
}
```

**分页响应结构**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "data": [
      { "id": 1, "name": "张三" },
      { "id": 2, "name": "李四" }
    ],
    "total": 100,
    "page": 1,
    "size": 10
  },
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

---

## 🔧 高级配置

### 配置文件

在 [`application.yml`](../../application.yml:1) 中配置：

```yaml
forgeboot:
  webmvc:
    response:
      # 是否包含 requestId
      include-request-id: true
      # 默认成功码
      default-success-code: 200
      # 默认失败码
      default-failure-code: 500
```

### 自定义 InfoResolver

实现国际化消息解析：

```kotlin
@Component
class CustomInfoResolver(
    private val messageSource: MessageSource
) : InfoResolver {
    
    override fun resolve(info: InfoProvider): ResolvedInfo {
        val locale = LocaleContextHolder.getLocale()
        val message = messageSource.getMessage(
            "code.${info.code}",
            null,
            info.message,
            locale
        )
        return ResolvedInfo(info.code, message)
    }
}
```

### 自定义扩展字段

通过 [
`ExtraContributor`](forgeboot-webmvc-dto-api/src/main/kotlin/com/gewuyou/forgeboot/webmvc/dto/api/entities/ExtraContributor.kt:29)
动态添加字段：

```kotlin
@Component
class ServerInfoContributor : ExtraContributor {
    override fun contribute(): Map<String, Any> {
        return mapOf(
            "serverTime" to System.currentTimeMillis(),
            "version" to "1.0.0",
            "environment" to "production"
        )
    }
}
```

响应结果会自动包含这些字段：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000,
  "serverTime": 1728384000000,
  "version": "1.0.0",
  "environment": "production"
}
```

### 自定义 RequestId 提供者

```kotlin
@Component
class CustomRequestIdProvider : RequestIdProvider {
    override fun getRequestId(): String {
        // 从 MDC 或 ContextHolder 获取
        return MDC.get("traceId") ?: UUID.randomUUID().toString()
    }
}
```

---

## 📦 模块结构

```
forgeboot-webmvc-dto/
├── forgeboot-webmvc-dto-api/           # API 接口定义
│   ├── entities/                       # 响应实体
│   │   ├── ApiResponseFamily.kt        # 响应类型族（Success/Failure）
│   │   ├── ApiResponses.kt             # 响应构建器接口
│   │   ├── PageResult.kt               # 分页结果
│   │   └── ExtraContributor.kt         # 扩展字段贡献者
│   ├── provider/                       # 提供者接口
│   │   └── InfoProvider.kt             # 信息提供者
│   └── config/                         # 配置类
│       └── ResponseProps.kt            # 响应配置属性
├── forgeboot-webmvc-dto-impl/          # 默认实现
│   └── DefaultApiResponses.kt          # 默认响应构建器
└── forgeboot-webmvc-dto-autoconfigure/ # 自动配置
    └── DtoAutoConfiguration.kt         # Spring Boot 自动配置
```

---

## 🎯 使用场景

| 场景        | 使用方式                     | 说明                 |
|-----------|--------------------------|--------------------|
| 返回单个对象    | `success(data)`          | 返回业务数据             |
| 返回列表      | `success(list)`          | 返回集合数据             |
| 返回分页结果    | `success(pageResult)`    | 返回分页数据             |
| 操作成功（无数据） | `successMessage(msg)`    | 仅返回成功消息            |
| 操作失败      | `failure(code, message)` | 返回错误码和错误消息         |
| 自定义响应码    | 修改 `ResponseProps`       | 配置默认成功/失败码         |
| 多语言支持     | 实现 `InfoResolver`        | 根据 locale 返回不同语言消息 |
| 添加全局响应字段  | 实现 `ExtraContributor`    | 动态添加如版本号、服务器时间等字段  |
| 请求追踪      | 配置 `RequestIdProvider`   | 注入 traceId 便于日志追踪  |

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建：

```bash
./gradlew :forgeboot-webmvc:forgeboot-webmvc-dto:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！