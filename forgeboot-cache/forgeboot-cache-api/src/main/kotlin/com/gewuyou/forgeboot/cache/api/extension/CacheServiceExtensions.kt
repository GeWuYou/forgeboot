package com.gewuyou.forgeboot.cache.api.extension

import com.gewuyou.forgeboot.cache.api.loader.CacheLoader
import com.gewuyou.forgeboot.cache.api.service.CacheService
import java.time.Duration

/**
 * 操作符扩展函数，用于通过 set(key, value, ttl) 存储带有过期时间的泛型值。
 *
 * 该函数扩展了 CacheService 接口，允许使用操作符语法设置缓存键值对，并指定生存时间。
 * 实际调用 put 方法完成缓存写入操作。
 *
 * @param key   缓存键，用于唯一标识存储的数据
 * @param value 值，支持任意对象类型，将被序列化后存储
 * @param ttl   缓存生存时间，指定该值后缓存将在设定的时间后自动失效
 */
operator fun CacheService.set(key: String, value: Any, ttl: Duration) {
    this.put(key, value, ttl)
}

/**
 * 获取指定键的缓存值。
 *
 * @param key  缓存键
 * @param type 值的类型 Class
 * @return 缓存中的对象，如果不存在则为 null
 */
operator fun <T : Any> CacheService.get(key: String, type: Class<T>): T? {
    return this.retrieve(key, type)
}

/**
 * 通过泛型方式获取指定键的缓存值。
 *
 * @param key 缓存键
 * @return 缓存中的对象，如果不存在则为 null
 */
inline fun <reified T : Any> CacheService.get(key: String): T? {
    return this.retrieve(key, T::class.java)
}
/**
 * 获取指定键的缓存值，若不存在则使用给定的加载器进行加载，并将结果存入缓存。
 *
 * @param key   缓存键
 * @param type  值的类型 Class
 * @param ttl   缓存生存时间，指定该值后缓存将在设定的时间后自动失效
 * @param loader 用于加载数据的 CacheLoader 实例
 * @return 缓存中的对象，如果不存在且 loader 也无法加载则为 null
 */
fun <T : Any> CacheService.getOrLoad(
    key: String,
    type: Class<T>,
    ttl: Duration,
    loader: CacheLoader<T> = CacheLoader { _, _ -> null }
): T? {
    return this.retrieve(key, type) ?: loader.load(key, type)?.also {
        this.put(key, it, ttl)
    }
}

/**
 * 通过泛型方式获取指定键的缓存值，若不存在则使用给定的加载函数进行加载，并将结果存入缓存。
 *
 * @param key 缓存键
 * @param ttl 缓存生存时间，指定该值后缓存将在设定的时间后自动失效
 * @param loader 一个函数，接收键并返回加载的数据（或 null）
 * @return 缓存中的对象，如果不存在且 loader 也无法加载则为 null
 */
inline fun <reified T : Any> CacheService.getOrLoad(
    key: String,
    ttl: Duration,
    loader: CacheLoader<T> = CacheLoader { _, _ -> null }
): T? {
    return retrieve(key, T::class.java) ?: loader.load(key, T::class.java)?.also {
        put(key, it, ttl)
    }
}
