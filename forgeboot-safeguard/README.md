# forgeboot-safeguard-spring-boot-starter

> 🛡️ 服务防护模块，提供限流、幂等、冷却、尝试次数限制等防护能力，基于 Redis 实现分布式场景下的高可用防护。

---

## 🧩 简介

`forgeboot-safeguard-spring-boot-starter` 是 ForgeBoot 提供的服务防护框架，通过注解驱动的方式为业务方法提供多维度防护能力。支持灵活的策略配置、SpEL
表达式解析、自定义异常工厂等特性。

该模块基于 Redis + Lua 脚本实现分布式防护，同时集成 Bucket4j 提供令牌桶算法支持，适用于高并发、分布式场景。

---

## ✨ 核心功能

- ✅ **限流（Rate Limit）**：基于令牌桶算法的分布式限流
- ✅ **幂等（Idempotency）**：防止重复提交，支持返回值缓存
- ✅ **冷却（Cooldown）**：操作冷却期控制，防止频繁调用
- ✅ **尝试次数限制（Attempt Limit）**：时间窗口内的操作次数限制
- ✅ **注解驱动**：通过 `@RateLimit`、`@Idempotent`、`@Cooldown`、`@AttemptLimit` 注解使用
- ✅ **SpEL 支持**：支持 SpEL 表达式动态解析 key 和参数
- ✅ **自定义异常**：可自定义异常工厂实现业务异常处理
- ✅ **指标监控**：提供 `SafeguardMetrics` 接口集成监控系统
- ✅ **键模板管理**：支持注册和管理防护键模板

---

## 📦 引入依赖

使用 Maven：

```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-safeguard-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

使用 Gradle：

```groovy
implementation "io.github.gewuyou:forgeboot-safeguard-spring-boot-starter:${version}"
```

---

## 🚀 快速开始

### ⚙️ 配置 Redis

在 [`application.yml`](../application.yml:1) 中配置 Redis 连接：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_password
    database: 0

forgeboot:
  safeguard:
    enabled: true
```

### 1️⃣ 限流（Rate Limit）

限制接口调用频率，防止流量过载：

```kotlin
@RestController
class OrderController(private val orderService: OrderService) {
    
    @PostMapping("/order/create")
    @RateLimit(
        key = "#userId",
        capacity = "100",           // 桶容量 100 个令牌
        refillTokens = "10",        // 每次补充 10 个令牌
        refillPeriodMs = "1000",    // 每 1 秒补充一次
        requested = "1"             // 每次请求消耗 1 个令牌
    )
    fun createOrder(@RequestParam userId: String, @RequestBody order: Order): ApiResponse {
        return orderService.create(userId, order)
    }
}
```

### 2️⃣ 幂等（Idempotency）

防止重复提交，支持返回值缓存：

```kotlin
@Service
class PaymentService {
    
    @Idempotent(
        key = "#orderId",
        ttlSeconds = "300",         // 幂等窗口 5 分钟
        mode = IdemMode.RETURN      // 重复请求返回缓存的结果
    )
    fun processPayment(orderId: String, amount: BigDecimal): PaymentResult {
        // 执行支付逻辑
        return PaymentResult(orderId, "SUCCESS")
    }
}
```

### 3️⃣ 冷却（Cooldown）

操作冷却期控制，防止频繁调用：

```kotlin
@Service
class SmsService {
    
    @Cooldown(
        key = "#phone",
        seconds = "60",             // 60 秒冷却期
        mode = KeyProcessingMode.ON_SUCCESS  // 成功后才启动冷却
    )
    fun sendVerificationCode(phone: String): Boolean {
        // 发送短信验证码
        return true
    }
}
```

### 4️⃣ 尝试次数限制（Attempt Limit）

限制时间窗口内的操作次数：

```kotlin
@Service
class LoginService {
    
    @AttemptLimit(
        key = "#username",
        window = "PT10M",           // 10 分钟时间窗口
        maxAttempts = "5",          // 最多尝试 5 次
        onFailure = true            // 只有失败时才计数
    )
    fun login(username: String, password: String): LoginResult {
        if (!validatePassword(username, password)) {
            throw InvalidCredentialsException()
        }
        return LoginResult.success(username)
    }
}
```

---

## 🔧 高级配置

### 自定义异常工厂

为不同的防护场景自定义异常：

```kotlin
@Component
class CustomRateLimitExceptionFactory : RateLimitExceptionFactory {
    override fun create(ctx: RateLimitContext): RuntimeException {
        return BusinessException(
            code = "RATE_LIMIT_EXCEEDED",
            message = "请求过于频繁，请稍后再试",
            details = mapOf(
                "key" to ctx.key.full(),
                "retryAfter" to ctx.retryAfterMs
            )
        )
    }
}
```

在注解中指定自定义工厂：

```kotlin
@RateLimit(
    key = "#userId",
    capacity = "100",
    refillTokens = "10",
    refillPeriodMs = "1000",
    factory = CustomRateLimitExceptionFactory::class
)
fun createOrder(userId: String): Order {
    // ...
}
```

### 使用键模板

注册和使用键模板简化配置：

```kotlin
@Configuration
class SafeguardKeyConfig(private val registry: KeyTemplateRegistry) {
    
    @PostConstruct
    fun registerTemplates() {
        // 注册模板：order:create:{userId}:{type}
        registry.register("order:create", "order:create:{userId}:{type}")
    }
}

// 使用模板
@RateLimit(
    key = "order:create",
    capacity = "100",
    refillTokens = "10",
    refillPeriodMs = "1000"
)
fun createOrder(@PathVariable userId: String, @PathVariable type: String): Order {
    // 键会自动解析为: order:create:123:NORMAL
}
```

### SpEL 表达式

支持强大的 SpEL 表达式：

```kotlin
@Service
class UserService {
    
    @RateLimit(
        key = "#user.id + ':' + #user.role",  // 组合键
        capacity = "#user.vip ? '1000' : '100'",  // 动态容量
        refillTokens = "10",
        refillPeriodMs = "1000"
    )
    fun updateProfile(user: User): User {
        // ...
    }
}
```

---

## 📦 模块结构

```
forgeboot-safeguard/
├── forgeboot-safeguard-core/           # 核心接口与模型
│   ├── api/                            # 防护接口定义
│   ├── model/                          # 上下文与结果模型
│   ├── policy/                         # 策略定义
│   ├── factory/                        # 异常工厂
│   └── key/                            # 键管理
├── forgeboot-safeguard-redis/          # Redis 实现
│   ├── ratelimit/                      # 限流实现（Lua + Bucket4j）
│   ├── idem/                           # 幂等实现
│   ├── cooldown/                       # 冷却实现
│   ├── attemptlimit/                   # 尝试次数限制实现
│   └── support/                        # Lua 脚本执行器
└── forgeboot-safeguard-autoconfigure/  # 自动配置
    ├── annotations/                    # 注解定义
    ├── aop/                            # AOP 切面
    └── resolver/                       # 异常工厂解析器
```

---

## 🎯 使用场景

| 场景         | 推荐功能            | 说明                    |
|------------|-----------------|-----------------------|
| API 接口限流   | `@RateLimit`    | 防止接口被恶意刷量             |
| 支付/下单防重    | `@Idempotent`   | 防止重复提交导致多次扣款/创建订单     |
| 短信/邮件发送限制  | `@Cooldown`     | 防止频繁发送验证码             |
| 登录失败次数限制   | `@AttemptLimit` | 防止暴力破解，超过次数后锁定账户      |
| 资源操作频率控制   | `@RateLimit`    | 限制用户在一定时间内的操作频率       |
| 敏感操作二次确认延迟 | `@Cooldown`     | 删除/修改重要数据需要冷却期后才能再次操作 |

---

## ⚙️ 构建方式

使用 Gradle 命令进行构建发布：

```bash
./gradlew :forgeboot-safeguard:build
```

---

## 📄 许可

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 开源协议。

---

## 📬 联系作者

由 [@gewuyou](https://github.com/gewuyou) 维护。

欢迎提交 Issue 或 PR 改进本模块！