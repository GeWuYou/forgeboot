# forgeboot-webmvc-exception

> 🚨 全局异常处理模块，提供统一的异常拦截、转换和响应机制，支持国际化异常消息。

---

## 🧩 简介

`forgeboot-webmvc-exception` 是 ForgeBoot WebMVC 系列中的异常处理模块，通过 `@ControllerAdvice` 和 `@ExceptionHandler`
提供全局异常拦截能力。支持自定义异常映射、国际化错误消息、异常堆栈追踪等功能，确保 API 返回统一的错误响应格式。

---

## ✨ 核心功能

- ✅ **全局异常拦截**：统一捕获并处理应用中的所有异常
- ✅ **异常映射**：将业务异常映射为标准化的 HTTP 响应
- ✅ **国际化支持**：根据请求语言返回对应的错误消息
- ✅ **堆栈追踪**：可配置是否返回异常堆栈信息（生产环境通常关闭）
- ✅ **自定义异常处理器**：支持扩展自定义异常处理逻辑
- ✅ **参数校验异常处理**：自动处理 `@Valid` 注解的校验异常
- ✅ **HTTP 状态码映射**：根据异常类型自动设置合适的 HTTP 状态码

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-webmvc-exception</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-webmvc-exception:${version}"
```

---

## 🚀 快速开始

### 1️⃣ 自动配置

引入依赖后，异常处理器会自动生效，无需额外配置。

### 2️⃣ 基础使用

**定义业务异常**：

```kotlin
/**
 * 业务异常基类
 */
open class BusinessException(
    val code: Int,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 资源未找到异常
 */
class ResourceNotFoundException(
    resourceName: String,
    resourceId: Any
) : BusinessException(
    code = 404,
    message = "资源未找到: $resourceName (ID: $resourceId)"
)

/**
 * 参数校验异常
 */
class ValidationException(
    message: String
) : BusinessException(
    code = 400,
    message = message
)
```

**在业务代码中抛出异常**：

```kotlin
@Service
class UserService {
    
    fun findById(id: Long): User {
        return userRepository.findById(id)
            ?: throw ResourceNotFoundException("User", id)
    }
    
    fun updateUser(id: Long, request: UpdateUserRequest) {
        if (request.age < 0) {
            throw ValidationException("年龄不能为负数")
        }
        // 更新逻辑
    }
}
```

**异常会被自动拦截并转换为统一响应**：

```json
{
  "code": 404,
  "message": "资源未找到: User (ID: 123)",
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

### 3️⃣ 参数校验异常处理

使用 `@Valid` 注解的校验异常会自动处理：

```kotlin
data class CreateUserRequest(
    @field:NotBlank(message = "用户名不能为空")
    val username: String,
    
    @field:Email(message = "邮箱格式不正确")
    val email: String,
    
    @field:Min(value = 18, message = "年龄必须大于等于18岁")
    val age: Int
)

@RestController
@RequestMapping("/api/users")
class UserController {
    
    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ApiResponse {
        // 如果校验失败，会自动返回错误响应
        return userService.create(request)
    }
}
```

**校验失败响应**：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "errors": {
    "username": "用户名不能为空",
    "email": "邮箱格式不正确",
    "age": "年龄必须大于等于18岁"
  },
  "requestId": "a1b2c3d4-e5f6-7890",
  "timestamp": 1728384000000
}
```

---

## 🔧 高级配置

### 配置文件

在 `application.yml` 中配置：

```yaml
forgeboot:
  webmvc:
    exception:
      # 是否包含堆栈追踪信息（生产环境建议关闭）
      include-stack-trace: false
      # 是否包含异常类名
      include-exception-class: true
      # 是否启用国际化
      enable-i18n: true
      # 默认错误码
      default-error-code: 500
```

### 自定义全局异常处理器

扩展全局异常处理逻辑：

```kotlin
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class CustomExceptionHandler(
    private val apiResponses: ApiResponses
) {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse> {
        val response = apiResponses.failure(ex.code, ex.message ?: "业务处理失败")
        return ResponseEntity
            .status(HttpStatus.valueOf(ex.code))
            .body(response)
    }
    
    /**
     * 处理数据库异常
     */
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<ApiResponse> {
        val response = apiResponses.failure(500, "数据库操作失败")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
    
    /**
     * 处理安全异常
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ApiResponse> {
        val response = apiResponses.failure(403, "无权访问该资源")
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(response)
    }
}
```

### 国际化异常消息

配置国际化资源文件：

**messages_zh_CN.properties**：

```properties
error.user.not.found=用户未找到
error.invalid.credentials=用户名或密码错误
error.access.denied=权限不足
```

**messages_en_US.properties**：

```properties
error.user.not.found=User not found
error.invalid.credentials=Invalid username or password
error.access.denied=Access denied
```

**使用国际化消息**：

```kotlin
@Service
class UserService(
    private val messageSource: MessageSource
) {
    
    fun findById(id: Long): User {
        return userRepository.findById(id)
            ?: throw ResourceNotFoundException(
                messageSource.getMessage(
                    "error.user.not.found",
                    null,
                    LocaleContextHolder.getLocale()
                )
            )
    }
}
```

### 异常与 HTTP 状态码映射

```kotlin
@Component
class ExceptionStatusMapper {
    
    fun mapToHttpStatus(exception: Exception): HttpStatus {
        return when (exception) {
            is ResourceNotFoundException -> HttpStatus.NOT_FOUND
            is ValidationException -> HttpStatus.BAD_REQUEST
            is AuthenticationException -> HttpStatus.UNAUTHORIZED
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            is ConflictException -> HttpStatus.CONFLICT
            is RateLimitExceededException -> HttpStatus.TOO_MANY_REQUESTS
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
```

---

## 📦 模块结构

```
forgeboot-webmvc-exception/
├── forgeboot-webmvc-exception-api/        # API 接口定义
│   └── exception/                         # 异常基类定义
├── forgeboot-webmvc-exception-impl/       # 默认实现
│   └── handler/                           # 全局异常处理器
└── forgeboot-webmvc-exception-autoconfigure/  # 自动配置
    ├── config/                            # 配置类
    └── properties/                        # 配置属性
```

---

## 🎯 常见异常处理场景

| 异常类型                              | HTTP 状态码 | 使用场景          |
|-----------------------------------|----------|---------------|
| `ResourceNotFoundException`       | 404      | 资源不存在         |
| `ValidationException`             | 400      | 参数校验失败        |
| `AuthenticationException`         | 401      | 未认证或认证失败      |
| `AccessDeniedException`           | 403      | 权限不足          |
| `ConflictException`               | 409      | 资源冲突（如重复创建）   |
| `RateLimitExceededException`      | 429      | 超过限流阈值        |
| `InternalServerException`         | 500      | 服务器内部错误       |
| `ServiceUnavailableException`     | 503      | 服务暂时不可用       |
| `MethodArgumentNotValidException` | 400      | Spring 参数校验失败 |

---

## 🛡️ 最佳实践

### 1. 异常分层

```kotlin
// 基础异常
open class BaseException(message: String, cause: Throwable? = null) 
    : RuntimeException(message, cause)

// 业务异常
open class BusinessException(code: Int, message: String) 
    : BaseException(message)

// 系统异常
open class SystemException(message: String, cause: Throwable? = null) 
    : BaseException(message, cause)
```

### 2. 异常信息不要暴露敏感数据

```kotlin
// ❌ 不好的做法
throw BusinessException("数据库连接失败: jdbc:mysql://localhost:3306/mydb?user=admin&password=123456")

// ✅ 好的做法
throw BusinessException("数据库连接失败，请联系管理员")
```

### 3. 生产环境关闭堆栈追踪

```yaml
forgeboot:
  webmvc:
    exception:
      # 生产环境设置为 false
      include-stack-trace: false
```

### 4. 记录异常日志

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ApiResponse {
        logger.error("Unhandled exception occurred", ex)
        return apiResponses.failure(500, "服务器内部错误")
    }
}
```

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建：

```bash
./gradlew :forgeboot-webmvc:forgeboot-webmvc-exception:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！