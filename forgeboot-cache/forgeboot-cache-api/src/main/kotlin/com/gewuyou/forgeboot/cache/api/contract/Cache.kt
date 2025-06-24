package com.gewuyou.forgeboot.cache.api.contract

import java.time.Duration

/**
 * 缓存接口定义
 *
 * 提供统一的缓存操作方法，包括获取、存储、删除和判断键是否存在。
 *
 * @since 2025-06-16 22:39:16
 * @author gewuyou
 */
interface Cache {

    /**
     * 获取指定键对应的缓存值。
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在则返回 null
     */
    fun retrieve(key: String): String?

    /**
     * 将键值对存储到缓存中，并设置过期时间。
     *
     * @param key   缓存键
     * @param value 缓存值，可以为 null
     * @param ttl   缓存存活时间（Time To Live）
     */
    fun put(key: String, value: String?, ttl: Duration)

    /**
     * 删除指定键的缓存。
     *
     * @param key 要删除的缓存键
     * @return 如果删除成功返回 true，否则返回 false
     */
    fun remove(key: String): Boolean

    /**
     * 判断指定键是否存在于缓存中。
     *
     * @param key 缓存键
     * @return 如果存在返回 true，否则返回 false
     */
    fun exists(key: String): Boolean
    /**
     * 清空缓存中的所有数据。
     * @param namespace 命名空间
     */
    fun clear(namespace: String)

    /**
     * 通过操作符重载实现缓存的获取操作。
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在则返回 null
     */
    fun Cache.get(key: String): String? = this.retrieve(key)

    /**
     * 通过操作符重载实现缓存的存储操作。
     *
     * @param key   缓存键
     * @param value 缓存值，可以为 null
     * @param ttl   缓存存活时间（Time To Live）
     */
    operator fun Cache.set(key: String, value: String?, ttl: Duration) = this.put(key, value, ttl)
}
