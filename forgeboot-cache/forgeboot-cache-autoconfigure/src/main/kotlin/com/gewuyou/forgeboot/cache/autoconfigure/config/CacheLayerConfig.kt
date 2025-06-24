package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.entities.CacheLayer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存层配置
 *
 * 该配置类用于定义多级缓存的层级结构，当前实现中包含两级缓存：
 * 第一层为基于 Caffeine 的本地缓存，具有每个条目独立的生存时间（TTL）特性；
 * 第二层为基于 Redis 的分布式缓存，用于跨服务共享缓存数据。
 *
 * @since 2025-06-21 11:39:35
 * @author gewuyou
 */
@Configuration
class CacheLayerConfig {

    /**
     * 定义缓存层级结构，Caffeine 缓存在第一层，Redis 缓存在第二层
     *
     * 该方法创建并返回一个包含两个缓存层的列表。每层缓存都与一个具体的缓存实现绑定，
     * 并指定其在整体架构中的优先级顺序。第一层使用本地的 Caffeine 缓存以提高访问速度，
     * 第二层使用 Redis 缓存来保证数据的共享和持久性。
     *
     * @param redisNullAwareCache 第二层缓存，基于 Redis 的 Null-aware 缓存实现
     * @param perEntryTtlCaffeineNullAwareCache 第一层缓存，基于 Caffeine 的 Null-aware 缓存实现，支持每个缓存条目独立的 TTL 设置
     * @return 包含两个缓存层的列表，按优先级排序
     */
    @Bean
    fun cacheLayers(
        @Qualifier("redisNullAwareCache") redisNullAwareCache: Cache,
        @Qualifier("perEntryTtlCaffeineNullAwareCache") perEntryTtlCaffeineNullAwareCache: Cache,
    ): List<CacheLayer> {
        return listOf(
            CacheLayer(perEntryTtlCaffeineNullAwareCache, 1), // 第一层：Caffeine 缓存，速度快，适合高频访问数据
            CacheLayer(redisNullAwareCache, 2) // 第二层：Redis 缓存，用于持久化存储和跨节点共享数据
        )
    }
}