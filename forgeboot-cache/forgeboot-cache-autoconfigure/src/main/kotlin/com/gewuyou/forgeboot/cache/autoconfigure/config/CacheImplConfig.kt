package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.policy.CachePolicy
import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy
import com.gewuyou.forgeboot.cache.impl.contract.MultiPolicyCache
import com.gewuyou.forgeboot.cache.impl.contract.PerEntryTtlCaffeineCache
import com.gewuyou.forgeboot.cache.impl.contract.RedisCache
import com.gewuyou.forgeboot.cache.impl.utils.RedisKeyScanner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * 缓存实现配置类
 *
 * 该配置类定义了缓存相关的 Bean，包括基于 Redis 和 Caffeine 的 NullAwareCache 实例。
 *
 * @since 2025-06-21 11:38:18
 * @author gewuyou
 */
@Configuration
class CacheImplConfig {

    /**
     * 构建基于 Redis 的 NullAwareCache 缓存实例
     *
     * 该方法创建了一个包装 RedisCache 的 NullAwareCache 实例，并结合 NullValuePolicy 控制 null 值的处理策略。
     *
     * @param redisTemplate Redis 模板，用于操作 Redis 数据库
     * @param redisKeyScanner 用于扫描 Redis 中的键
     * @param keyGenerator 用于生成缓存键
     * @param nullCachePolicy NullValuePolicy 实例，控制 null 值的缓存行为
     * @return 返回一个基于 Redis 的 Cache 实例
     */
    @Bean("redisNullAwareCache")
    fun redisNullAwareCache(
        redisTemplate: StringRedisTemplate,
        redisKeyScanner: RedisKeyScanner,
        keyGenerator: KeyGenerator,
        nullCachePolicy: NullValuePolicy,
        policies: List<CachePolicy>
    ): Cache {
        return MultiPolicyCache(RedisCache(redisTemplate, redisKeyScanner, keyGenerator), nullCachePolicy,policies)
    }

    /**
     * 构建基于 Caffeine 的 NullAwareCache 缓存实例
     *
     * 该方法创建了一个使用 PerEntryTtlCaffeineCache 的 NullAwareCache 实例，支持条目级别的 TTL（生存时间）设置。
     *
     * @param keyGenerator 用于生成缓存键
     * @param nullCachePolicy NullValuePolicy 实例，控制 null 值的缓存行为
     * @return 返回一个基于 Caffeine 的 Cache 实例
     */
    @Bean("perEntryTtlCaffeineNullAwareCache")
    fun perEntryTtlCaffeineNullAwareCache(
        keyGenerator: KeyGenerator,
        nullCachePolicy: NullValuePolicy,
        policies: List<CachePolicy>
    ): Cache {
        return MultiPolicyCache(PerEntryTtlCaffeineCache(keyGenerator), nullCachePolicy,policies)
    }
}
