package com.gewuyou.forgeboot.cache.impl.contract

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.impl.utils.RedisKeyScanner
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

/**
 * Redis 缓存实现类，用于通过 Redis 存储、获取和管理缓存数据。
 *
 * @property redisTemplate Spring 提供的 StringRedisTemplate 实例，用于与 Redis 进行交互
 * @property redisKeyScanner 用于扫描匹配的 Redis 键集合
 * @author gewuyou
 * @since 2025-06-17 21:08:36
 */
class RedisCache(
    private val redisTemplate: StringRedisTemplate,
    private val redisKeyScanner: RedisKeyScanner,
    private val keyGenerator: KeyGenerator
) : Cache {
    /**
     * 获取指定键对应的缓存值。
     *
     * @param key 缓存键，非空字符串
     * @return 缓存值，如果不存在则返回 null
     */
    override fun retrieve(key: String): String? {
        return redisTemplate.opsForValue()[key]
    }

    /**
     * 将键值对存储到缓存中，并设置过期时间。
     *
     * @param key   缓存键，非空字符串
     * @param value 缓存值，可以为 null
     * @param ttl   缓存存活时间（Time To Live），单位为秒或毫秒等
     */
    override fun put(key: String, value: String?, ttl: Duration) {
        redisTemplate.opsForValue()[key, value!!] = ttl
    }

    /**
     * 删除指定键的缓存。
     *
     * @param key 要删除的缓存键，非空字符串
     * @return 如果删除成功返回 true，否则返回 false
     */
    override fun remove(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    /**
     * 判断指定键是否存在于缓存中。
     *
     * @param key 缓存键，非空字符串
     * @return 如果存在返回 true，否则返回 false
     */
    override fun exists(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    /**
     * 清空缓存中的所有数据，基于给定的命名空间和连接符进行模式匹配。
     *
     * @param namespace 命名空间，用于限定要清除的键范围
     */
    override fun clear(namespace: String) {
        require(namespace.isNotBlank()) { "namespace must not be blank" }
        // 构造匹配模式
        val pattern = "$namespace${keyGenerator.getConnectors()}*"
        while (true) {
            // 扫描当前模式下的所有键
            val keys = redisKeyScanner.scan(pattern)
            if (keys.isEmpty()) break
            // 批量删除扫描到的键
            redisTemplate.delete(keys)
        }
    }
}