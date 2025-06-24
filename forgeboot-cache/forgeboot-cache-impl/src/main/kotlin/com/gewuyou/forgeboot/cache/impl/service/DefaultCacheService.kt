package com.gewuyou.forgeboot.cache.impl.service

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.extension.get
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.service.CacheService
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import java.time.Duration


/**
 * 默认缓存服务实现类，提供基于前缀的键值对缓存操作。
 *
 * 该类实现了通用的缓存功能，包括获取、设置、删除、判断存在性和清空缓存的方法。
 * 所有方法均通过命名空间隔离不同业务的数据，确保缓存管理的安全性和灵活性。
 *
 * @property serializer 序列化/反序列化缓存值的工具
 * @property cache 实际存储数据的复合缓存实例
 * @property namespace 命名空间
 * @property cacheProperties 缓存配置属性，包含默认过期时间等
 * @since 2025-06-16 22:56:49
 * @author gewuyou
 */
class DefaultCacheService(
    private val serializer: ValueSerializer,
    private val cache: Cache,
    private val namespace: String,
    private val cacheProperties: CacheProperties,
    private val keyGenerator: KeyGenerator,
) : CacheService {

    /**
     * 获取指定键的缓存值。
     *
     * 在命名空间下拼接 key，并从缓存中获取对应的值。若值存在，则使用序列化器将其转换为指定类型；否则返回 null。
     *
     * @param key 缓存键
     * @param type 值的类型 Class
     * @return 缓存中的对象，如果不存在则为 null
     */
    override fun <T : Any> retrieve(key: String, type: Class<T>): T? {
        return cache[keyGenerator.generateKey(namespace,key)]?.let { serializer.deserialize(it, type) }
    }

    /**
     * 设置缓存值。
     *
     * 将值序列化后，在命名空间下拼接 key 并写入缓存。支持自定义 TTL（生存时间），若未指定则使用默认 TTL。
     *
     * @param key 缓存键
     * @param value 值（支持任意对象）
     * @param ttl 缓存时长，null 表示使用默认 TTL
     */
    override fun put(key: String, value: Any, ttl: Duration?) {
        cache.put(keyGenerator.generateKey(namespace,key), serializer.serialize(value), (ttl ?: cacheProperties.theDefaultCacheTTL))
    }

    /**
     * 删除缓存。
     *
     * 在命名空间下拼接 key，并尝试从缓存中移除对应条目。
     *
     * @param key 缓存键
     * @return 删除成功返回 true
     */
    override fun remove(key: String): Boolean {
        return cache.remove(keyGenerator.generateKey(namespace,key))
    }

    /**
     * 判断缓存是否存在。
     *
     * 检查在命名空间下拼接的 key 是否存在于缓存中。
     *
     * @param key 缓存键
     * @return 如果存在返回 true，否则返回 false
     */
    override fun exists(key: String): Boolean {
        return cache.exists(keyGenerator.generateKey(namespace,key))
    }

    /**
     * 清空指定命名空间下的所有缓存数据。
     *
     * 直接调用底层缓存实现的 clear 方法，传入命名空间参数。
     *
     * @param namespace 命名空间
     */
    override fun clear(namespace: String) {
        cache.clear(namespace)
    }
}
