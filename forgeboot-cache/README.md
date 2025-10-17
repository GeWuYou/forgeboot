# forgeboot-cache

Spring Boot 缓存自动配置模块，提供两级缓存（Caffeine + Redis）解决方案

## 简介

`forgeboot-cache` 是一个完整的缓存框架模块，提供基于 Caffeine 和 Redis
的两级缓存支持，封装了常用的缓存接口、默认实现及自动配置。该模块支持注解式和编程式两种使用方式，适用于高并发场景下的性能优化需求。

## 模块结构

```
forgeboot-cache/
├── forgeboot-cache-api/          # 缓存接口定义层
│   ├── annotations/               # 缓存注解（@CacheableEx、@CacheEvictEx、@CachePutEx）
│   ├── contract/                  # 缓存契约接口
│   ├── manager/                   # 缓存管理器接口
│   ├── service/                   # 缓存服务接口
│   └── config/                    # 配置属性类
├── forgeboot-cache-impl/          # 缓存实现层
│   ├── contract/                  # 具体缓存实现（Caffeine、Redis、组合缓存）
│   ├── manager/                   # 缓存管理器实现
│   ├── service/                   # 缓存服务实现
│   ├── aspect/                    # AOP 切面实现
│   └── policy/                    # 缓存策略实现
└── forgeboot-cache-autoconfigure/ # Spring Boot 自动配置
    └── config/                    # 各类配置类（基础、策略、实现、管理器等）
```

## 核心功能

- **多级缓存架构**：支持 Caffeine（本地缓存）+ Redis（分布式缓存）两级缓存
- **灵活的注解支持**：提供 `@CacheableEx`、`@CacheEvictEx`、`@CachePutEx` 注解
- **编程式 API**：通过 `CacheManager` 和 `CacheService` 接口直接操作缓存
- **SpEL 表达式支持**：缓存键支持 SpEL 表达式动态生成
- **空值缓存策略**：可配置是否缓存 null 值，防止缓存穿透
- **缓存预热机制**：支持应用启动时自动加载热点数据
- **分布式锁服务**：提供跨节点资源同步机制
- **灵活的 TTL 配置**：支持全局和方法级别的缓存过期时间设置

## 引入依赖

### Maven
```xml
<dependency>
  <groupId>io.github.gewuyou</groupId>
  <artifactId>forgeboot-cache-spring-boot-starter</artifactId>
    <version>${forgeboot.version}</version>
</dependency>
```

### Gradle Kotlin DSL

```kotlin
implementation("io.github.gewuyou:forgeboot-cache-spring-boot-starter:${forgebootVersion}")
```

### Gradle Groovy
```groovy
implementation "io.github.gewuyou:forgeboot-cache-spring-boot-starter:${forgebootVersion}"
```

## 配置说明

### 基础配置

在 `application.yml` 或 `application.properties` 中配置：

```yaml
forgeboot:
  cache:
    # 默认缓存过期时间（默认 15 分钟）
    the-default-cache-ttl: 15m

    # null 值占位符（默认 "__NULL__"）
    null-value-placeholder: "__NULL__"

    # Redis 缓存配置
    redis:
      enabled: true  # 是否启用 Redis 缓存（默认 true）

    # Caffeine 本地缓存配置
    caffeine:
      enabled: true  # 是否启用 Caffeine 缓存（默认 true）
```

### Redis 配置

需要配置 Spring Data Redis 的连接信息：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your-password  # 如果有密码
      database: 0
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

## 使用方式

### 方式一：注解式使用（推荐）

#### 1. @CacheableEx - 缓存查询结果

```kotlin
import com.gewuyou.forgeboot.cache.api.annotations.CacheableEx
import org.springframework.stereotype.Service

@Service
class UserService {

    /**
     * 缓存用户信息
     * - namespace: 缓存命名空间，用于区分不同业务
     * - keySpEL: 使用 SpEL 表达式生成缓存键
     * - ttl: 缓存过期时间（秒）
     * - cacheNull: 是否缓存 null 值（防止缓存穿透）
     * - type: 缓存值的类型
     */
    @CacheableEx(
        namespace = "user",
        keySpEL = "#userId",
        ttl = 3600L,  // 1小时
        cacheNull = true,
        type = User::class
    )
    fun getUserById(userId: Long): User? {
        // 从数据库查询用户
        return userRepository.findById(userId).orElse(null)
    }

    /**
     * 使用复合键
     */
    @CacheableEx(
        namespace = "user:profile",
        keySpEL = "#userId + ':' + #type",
        ttl = 1800L,
        type = UserProfile::class
    )
    fun getUserProfile(userId: Long, type: String): UserProfile {
        return userProfileRepository.findByUserIdAndType(userId, type)
    }
}
```

#### 2. @CachePutEx - 更新缓存

```kotlin
@Service
class UserService {

    /**
     * 更新用户信息并同步缓存
     */
    @CachePutEx(
        namespace = "user",
        keySpEL = "#user.id",
        ttl = 3600L
    )
    fun updateUser(user: User): User {
        // 更新数据库
        return userRepository.save(user)
    }
}
```

#### 3. @CacheEvictEx - 删除缓存

```kotlin
@Service
class UserService {

    /**
     * 删除用户并清除缓存
     */
    @CacheEvictEx(
        namespace = "user",
        keySpEL = "#userId"
    )
    fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    /**
     * 批量删除时清除多个缓存
     */
    @CacheEvictEx(
        namespace = "user",
        keySpEL = "#userIds.![toString()].join(',')"
    )
    fun deleteUsers(userIds: List<Long>) {
        userRepository.deleteAllById(userIds)
    }
}
```

### 方式二：编程式使用

#### 1. 通过 CacheManager

```kotlin
import com.gewuyou.forgeboot.cache.api.manager.CacheManager
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ProductService(
    private val cacheManager: CacheManager
) {

    fun getProduct(productId: Long): Product? {
        // 获取指定命名空间的缓存服务
        val cache = cacheManager.getCache("product")

        // 尝试从缓存获取
        val cacheKey = "product:$productId"
        var product = cache.retrieve(cacheKey, Product::class.java)

        if (product == null) {
            // 缓存未命中，从数据库查询
            product = productRepository.findById(productId).orElse(null)

            // 写入缓存
            if (product != null) {
                cache.put(cacheKey, product, Duration.ofHours(2))
            }
        }

        return product
    }

    fun updateProduct(product: Product) {
        // 更新数据库
        productRepository.save(product)

        // 更新缓存
        val cache = cacheManager.getCache("product")
        cache.put("product:${product.id}", product, Duration.ofHours(2))
    }

    fun deleteProduct(productId: Long) {
        // 删除数据库记录
        productRepository.deleteById(productId)

        // 删除缓存
        val cache = cacheManager.getCache("product")
        cache.remove("product:$productId")
    }

    fun clearProductCache() {
        // 清除 product 命名空间下的所有缓存
        cacheManager.clear("product")
    }

    fun clearAllCache() {
        // 清除所有缓存
        cacheManager.clearAll()
    }
}
```

#### 2. 直接注入 CacheService

```kotlin
import com.gewuyou.forgeboot.cache.api.service.CacheService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class OrderService(
    @Qualifier("orderCache") private val orderCache: CacheService
) {

    fun getOrder(orderId: String): Order? {
        val cacheKey = "order:$orderId"

        // 检查缓存是否存在
        if (orderCache.exists(cacheKey)) {
            return orderCache.retrieve(cacheKey, Order::class.java)
        }

        // 查询数据库
        val order = orderRepository.findById(orderId).orElse(null)

        // 写入缓存（使用默认 TTL）
        if (order != null) {
            orderCache.put(cacheKey, order)
        }

        return order
    }
}
```

## SpEL 表达式示例

缓存键支持丰富的 SpEL 表达式：

```kotlin
// 1. 简单参数
@CacheableEx(namespace = "user", keySpEL = "#id")
fun getUser(id: Long): User

// 2. 对象属性
@CacheableEx(namespace = "user", keySpEL = "#user.id")
fun saveUser(user: User): User

// 3. 多参数组合
@CacheableEx(namespace = "order", keySpEL = "#userId + ':' + #status")
fun getOrders(userId: Long, status: String): List<Order>

// 4. 方法调用
@CacheableEx(namespace = "user", keySpEL = "#user.getId().toString()")
fun updateUser(user: User): User

// 5. 集合操作
@CacheableEx(namespace = "user", keySpEL = "#ids.![toString()].join(',')")
fun getUsers(ids: List<Long>): List<User>

// 6. 条件表达式
@CacheableEx(namespace = "product", keySpEL = "#price > 100 ? 'expensive' : 'cheap'")
fun getProductsByPrice(price: Double): List<Product>
```

## 缓存预热

实现 `CacheWarmUpService` 接口来自定义缓存预热逻辑：

```kotlin
import com.gewuyou.forgeboot.cache.api.service.CacheWarmUpService
import org.springframework.stereotype.Component

@Component
class UserCacheWarmUp(
    private val cacheManager: CacheManager,
    private val userRepository: UserRepository
) : CacheWarmUpService {

    override fun warmUp() {
        // 应用启动时预加载热点用户数据
        val hotUsers = userRepository.findTop100ByOrderByLoginCountDesc()
        val cache = cacheManager.getCache("user")

        hotUsers.forEach { user ->
            cache.put("user:${user.id}", user, Duration.ofHours(1))
        }
    }
}
```

## 分布式锁使用

```kotlin
import com.gewuyou.forgeboot.cache.api.service.LockService
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class InventoryService(
    private val lockService: LockService
) {

    fun decreaseStock(productId: Long, quantity: Int): Boolean {
        val lockKey = "lock:product:$productId"

        // 尝试获取分布式锁
        return lockService.tryLock(
            key = lockKey,
            waitTime = Duration.ofSeconds(3),
            leaseTime = Duration.ofSeconds(10)
        ) {
            // 在锁保护下执行业务逻辑
            val product = productRepository.findById(productId).orElse(null)
            if (product != null && product.stock >= quantity) {
                product.stock -= quantity
                productRepository.save(product)
                true
            } else {
                false
            }
        }
    }
}
```

## CacheService API 参考

```kotlin
interface CacheService {

    /**
     * 获取缓存值
     * @param key 缓存键
     * @param type 值的类型
     * @return 缓存值，不存在返回 null
     */
    fun <T : Any> retrieve(key: String, type: Class<T>): T?

    /**
     * 设置缓存值
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 过期时间，null 使用默认 TTL
     */
    fun put(key: String, value: Any, ttl: Duration? = null)

    /**
     * 删除缓存
     * @param key 缓存键
     * @return 删除成功返回 true
     */
    fun remove(key: String): Boolean

    /**
     * 判断缓存是否存在
     * @param key 缓存键
     * @return 存在返回 true
     */
    fun exists(key: String): Boolean

    /**
     * 清空指定命名空间的缓存
     * @param namespace 命名空间
     */
    fun clear(namespace: String)
}
```

## 最佳实践

### 1. 命名空间规范

使用有意义的命名空间来组织缓存：

```kotlin
// 推荐
@CacheableEx(namespace = "user:profile", keySpEL = "#userId")
@CacheableEx(namespace = "product:detail", keySpEL = "#productId")
@CacheableEx(namespace = "order:list", keySpEL = "#userId")

// 不推荐
@CacheableEx(namespace = "cache", keySpEL = "#id")  // 命名空间太模糊
```

### 2. 合理设置 TTL

根据数据的更新频率设置合理的过期时间：

```kotlin
// 用户基本信息：1小时
@CacheableEx(namespace = "user", keySpEL = "#id", ttl = 3600L)

// 商品详情：30分钟
@CacheableEx(namespace = "product", keySpEL = "#id", ttl = 1800L)

// 热点数据：5分钟
@CacheableEx(namespace = "trending", keySpEL = "#category", ttl = 300L)
```

### 3. 防止缓存穿透

对于可能返回 null 的查询，启用 `cacheNull`：

```kotlin
@CacheableEx(
    namespace = "user",
    keySpEL = "#id",
    ttl = 300L,
    cacheNull = true  // 缓存 null 值，防止缓存穿透
)
fun getUserById(id: Long): User?
```

### 4. 缓存一致性

更新数据时及时更新或删除缓存：

```kotlin
@Service
class UserService {

    @CacheableEx(namespace = "user", keySpEL = "#id", type = User::class)
    fun getUser(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    @CachePutEx(namespace = "user", keySpEL = "#user.id")
    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    @CacheEvictEx(namespace = "user", keySpEL = "#id")
    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }
}
```

### 5. 避免缓存大对象

不要缓存过大的对象，可能导致性能问题：

```kotlin
// 不推荐：缓存整个列表
@CacheableEx(namespace = "users", keySpEL = "'all'")
fun getAllUsers(): List<User>  // 可能包含数万条记录

// 推荐：分页缓存
@CacheableEx(namespace = "users:page", keySpEL = "#page + ':' + #size")
fun getUsersByPage(page: Int, size: Int): Page<User>
```

## 注意事项

1. **序列化要求**：缓存的对象必须可序列化（实现 `Serializable` 接口或支持 JSON 序列化）
2. **Redis 连接**：确保 Redis 服务可用，否则应用启动会失败
3. **命名空间隔离**：不同业务使用不同的命名空间，避免缓存键冲突
4. **TTL 设置**：根据业务场景合理设置过期时间，避免缓存雪崩
5. **监控告警**：建议监控缓存命中率、Redis 连接状态等指标

## 故障排查

### 1. 缓存未生效

检查配置是否正确：

```yaml
forgeboot:
  cache:
    redis:
      enabled: true  # 确保启用
    caffeine:
      enabled: true
```

### 2. Redis 连接失败

检查 Redis 配置和网络连接：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      # 测试连接
```

### 3. SpEL 表达式错误

确保 SpEL 表达式语法正确，参数名与方法参数一致：

```kotlin
// 错误：参数名不匹配
@CacheableEx(namespace = "user", keySpEL = "#userId")
fun getUser(id: Long): User  // 参数名是 id，不是 userId

// 正确
@CacheableEx(namespace = "user", keySpEL = "#id")
fun getUser(id: Long): User
```

## 构建

```bash
./gradlew :forgeboot-cache:build
```

## 版本要求

- JDK 17+
- Spring Boot 3.0+
- Kotlin 1.9+
- Redis 5.0+（如果使用 Redis 缓存）

## 许可

Apache-2.0

---

## 相关链接

- [Spring Cache 文档](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Caffeine 文档](https://github.com/ben-manes/caffeine)
- [Spring Data Redis 文档](https://spring.io/projects/spring-data-redis)
