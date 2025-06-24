package com.gewuyou.forgeboot.cache.impl.contract

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

/**
 * 每个条目 TTL 咖啡因缓存实现类
 *
 * 该缓存为每个缓存条目设置独立的生存时间（TTL），基于 Caffeine 缓存策略设计。
 *
 * @since 2025-06-17 22:34:50
 * @author gewuyou
 */
class PerEntryTtlCaffeineCache(
    private val keyGenerator: KeyGenerator
) : Cache {

    /**
     * 缓存条目数据类，用于存储值和过期时间
     *
     * @property value 缓存的字符串值
     * @property expireAt 条目的过期时间戳（毫秒）
     */
    private data class CacheEntry(val value: String, val expireAt: Long)

    /**
     * 使用线程安全的 ConcurrentHashMap 存储缓存条目
     */
    private val map = ConcurrentHashMap<String, CacheEntry>(16)

    /**
     * 协程作用域，用于执行后台清理任务
     */
    private val cleanupScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * 初始化块：启动定期清理任务
     *
     * 清理任务每 5 秒运行一次，移除所有已过期的缓存条目。
     */
    init {
        cleanupScope.launch {
            while (this.isActive) {
                val now = System.currentTimeMillis()
                map.entries.removeIf { it.value.expireAt <= now }
                delay(5.seconds)
            }
        }
    }

    /**
     * 获取指定键的缓存值
     *
     * 如果缓存不存在或已过期，则返回 null 并从缓存中移除该条目。
     *
     * @param key 要获取的缓存键
     * @return 缓存值（如果存在且未过期），否则 null
     */
    override fun retrieve(key: String): String? {
        val entry = map[key] ?: return null
        return if (System.currentTimeMillis() > entry.expireAt) {
            map.remove(key)
            null
        } else entry.value
    }

    /**
     * 将键值对放入缓存，并设置其生存时间
     *
     * 如果值为 null，则不会插入任何内容。
     *
     * @param key 键
     * @param value 值
     * @param ttl 生存时间（Duration 类型）
     */
    override fun put(key: String, value: String?, ttl: Duration) {
        if (value != null) {
            val expireAt = System.currentTimeMillis() + ttl.toMillis()
            map[key] = CacheEntry(value, expireAt)
        }
    }

    /**
     * 从缓存中移除指定键
     *
     * @param key 要移除的键
     * @return 如果成功移除则返回 true，否则 false
     */
    override fun remove(key: String): Boolean = map.remove(key) != null

    /**
     * 检查指定键是否存在有效的缓存条目
     *
     * @param key 要检查的键
     * @return 如果存在有效条目则返回 true，否则 false
     */
    override fun exists(key: String): Boolean = retrieve(key) != null

    /**
     * 清除指定命名空间下的所有缓存条目
     *
     * 根据命名空间和连接符生成前缀，移除所有以该前缀开头的键。
     *
     * @param namespace 命名空间
     */
    override fun clear(namespace: String) {
        val prefix = "$namespace${keyGenerator.getConnectors()}"
        map.keys.removeIf { it.startsWith(prefix) }
    }
}