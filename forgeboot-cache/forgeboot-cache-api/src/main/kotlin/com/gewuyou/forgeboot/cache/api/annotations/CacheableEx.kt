package com.gewuyou.forgeboot.cache.api.annotations

import kotlin.reflect.KClass

/**
 * 缓存注解扩展，用于标记可缓存的方法。
 *
 * 该注解应用于方法级别，表示该方法的结果可以被缓存。通过指定不同的参数，
 * 可以控制缓存的命名空间、键生成表达式、过期时间、是否缓存空值以及缓存层级。
 *
 * @property namespace 缓存的命名空间，用于区分不同业务场景下的缓存数据，默认为空字符串。
 * @property keySpEL 缓存键的 SpEL 表达式，用于动态生成缓存键，默认为空字符串。
 * @property ttl 缓存过期时间，单位为秒，默认值为 300 秒（即 10 分钟）。
 * @property cacheNull 是否缓存空值，默认为 false，表示不缓存空结果。
 * @property type 指定缓存值的类型，用于确保类型安全，默认使用 KClass<*> 表示任意类型。
 *
 * @since 2025-06-16
 * @author gewuyou
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CacheableEx(
    val namespace: String,
    val keySpEL: String,
    val ttl: Long = 300L,
    val cacheNull: Boolean = false,
    @JvmSuppressWildcards val type: KClass<*> = Any::class
)