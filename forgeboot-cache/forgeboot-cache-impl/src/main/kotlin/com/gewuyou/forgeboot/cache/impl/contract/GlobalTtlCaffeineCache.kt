package com.gewuyou.forgeboot.cache.impl.contract

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * 全局 TTL 咖啡因缓存
 *
 * 该实现基于 Caffeine 缓存库，所有缓存项使用统一的默认过期时间（TTL）。
 * 不支持针对单个缓存项设置不同的 TTL。
 *
 * @property cacheProperties 缓存配置属性，用于获取默认 TTL 设置
 * @property keyGenerator 键生成器，用于构建带命名空间连接符的键前缀
 * @since 2025-06-17 22:32:36
 * @author gewuyou
 */
class GlobalTtlCaffeineCache(
    cacheProperties: CacheProperties,
    private val keyGenerator: KeyGenerator
) : Cache {

    /**
     * 初始化 Caffeine 缓存实例
     *
     * 使用 expireAfterWrite 设置写入后过期时间，时间长度基于 cacheProperties.theDefaultCacheTTL 配置。
     * 最大缓存条目数量限制为 10,000。
     */
    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(cacheProperties.theDefaultCacheTTL.toMillis(), TimeUnit.MILLISECONDS)
        .maximumSize(10_000)
        .build<String, String>()

    /**
     * 获取指定 key 对应的缓存值
     *
     * @param key 缓存键
     * @return 缓存中对应的值，如果不存在则返回 null
     */
    override fun retrieve(key: String): String? = cache.getIfPresent(key)

    /**
     * 将键值对放入缓存中
     *
     * 注意：此处忽略传入的 ttl 参数，统一使用 defaultTtl 控制过期时间。
     *
     * @param key   缓存键
     * @param value 缓存值，若为 null 则不存储
     * @param ttl   忽略此参数，使用全局默认的过期时间
     */
    override fun put(key: String, value: String?, ttl: Duration) {
        if (value != null) cache.put(key, value)
    }

    /**
     * 移除指定 key 的缓存项
     *
     * @param key 要移除的缓存键
     * @return 总是返回 true，表示操作成功
     */
    override fun remove(key: String): Boolean {
        cache.invalidate(key)
        return true
    }

    /**
     * 检查指定 key 是否存在于缓存中
     *
     * @param key 缓存键
     * @return 如果存在则返回 true，否则返回 false
     */
    override fun exists(key: String): Boolean = cache.getIfPresent(key) != null

    /**
     * 清除指定命名空间下的所有缓存项
     *
     * 根据命名空间和连接符拼接出前缀，并清除所有以前缀开头的 key。
     *
     * @param namespace 命名空间前缀
     */
    override fun clear(namespace: String) {
        val prefix = "$namespace${keyGenerator.getConnectors()}"
        cache.asMap().keys.filter { it.startsWith(prefix) }.forEach { cache.invalidate(it) }
    }
}