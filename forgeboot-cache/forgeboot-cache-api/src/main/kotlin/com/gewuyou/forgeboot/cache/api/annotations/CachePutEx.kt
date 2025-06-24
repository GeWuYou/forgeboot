package com.gewuyou.forgeboot.cache.api.annotations

/**
 * 缓存 put ex
 *
 * 用于声明式缓存更新操作的注解，适用于方法级别
 *
 * @property namespace 命名空间，用于缓存键的逻辑分组，默认为空字符串
 * @property keySpEL 缓存键的 SpEL 表达式，默认为空字符串
 * @property ttl 缓存键生存时间
 *
 * @since 2025-06-16 22:33:35
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CachePutEx(
    val namespace: String,
    val keySpEL: String,
    val ttl: Long = 300L
)