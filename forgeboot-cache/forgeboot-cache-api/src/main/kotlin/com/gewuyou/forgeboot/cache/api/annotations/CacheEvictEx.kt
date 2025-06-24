package com.gewuyou.forgeboot.cache.api.annotations

/**
 * 缓存移除扩展注解，用于标识需要清除缓存的方法
 *
 * 该注解应用于方法上，表示在方法执行前后可以触发缓存清除操作。
 * 支持通过命名空间和SpEL表达式定义缓存键，并指定缓存层级。
 *
 * @property namespace 缓存的命名空间，用于对缓存进行逻辑分组，默认为空字符串
 * @property keySpEL 缓存键的SpEL表达式，用于动态生成缓存键，默认为空字符串
 *
 * @since 2025-06-16 22:29:52
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheEvictEx(
    val namespace: String,
    val keySpEL: String
)