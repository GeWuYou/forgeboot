package com.gewuyou.forgeboot.cache.api.service

import java.time.Duration


/**
 * 缓存服务接口，定义了缓存的基本操作。
 *
 * @since 2025-06-16 21:58:56
 * @author gewuyou
 */
interface CacheService {

    /**
     * 获取指定键的缓存值。
     *
     * @param key  缓存键
     * @param type 值的类型 Class
     * @return 缓存中的对象，如果不存在则为 null
     */
    fun <T : Any> retrieve(key: String, type: Class<T>): T?

    /**
     * 设置缓存值。
     *
     * @param key   缓存键
     * @param value 值（支持任意对象）
     * @param ttl   缓存时长，null 表示使用默认 TTL
     */
    fun put(key: String, value: Any, ttl: Duration? = null)

    /**
     * 删除缓存。
     *
     * @param key 缓存键
     * @return 删除成功返回 true
     */
    fun remove(key: String): Boolean

    /**
     * 判断缓存是否存在。
     *
     * @param key 缓存键
     * @return 如果存在返回 true，否则返回 false
     */
    fun exists(key: String): Boolean

    /**
     * 清空指定命名空间下的缓存。
     *
     * @param namespace 命名空间
     */
    fun clear(namespace: String)
}