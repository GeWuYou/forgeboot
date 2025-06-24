package com.gewuyou.forgeboot.cache.autoconfigure

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.autoconfigure.config.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

/**
 * 缓存自动配置类
 *
 * 该类通过组合多个配置类实现完整的缓存功能装配，负责集成并协调以下模块：
 * - Redis 基础设施：连接工厂与操作模板（[CacheBaseConfig]）
 * - 缓存策略定义：该配置类用于定义缓存相关的策略 Bean（[CachePolicyConfig]）
 * - 缓存实现层：基于 Redis 和 Caffeine 的两级缓存支持（[CacheImplConfig]）
 * - 多级缓存架构：支持本地缓存与分布式缓存的协同工作（[CacheLayerConfig]）
 * - 组合缓存配置：用于构建复合缓存操作逻辑（[CacheCompositeConfig]）
 * - 序列化管理：统一处理键值序列化与反序列化（[CacheSerializerConfig]）
 * - 缓存管理器：提供统一访问入口和运行时管理能力（[CacheManagerConfig]）
 * - 缓存预热机制：系统启动后自动加载热点数据（[CacheWarmUpConfig]）
 * - 缓存切面支持：为注解如 @CacheableEx, @CacheEvictEx 提供执行上下文（[CacheAspectConfig]）
 * - 分布式锁服务：提供跨节点资源同步机制（[LockServiceConfig]）
 *
 * 所有导入的配置类共同构成完整的缓存解决方案，适用于高并发场景下的性能优化需求。
 *
 * @property CacheProperties 提供外部可配置参数，用于定制缓存行为
 *
 * @since 2025-06-17 20:49:17
 * @author gewuyou
 */
@Import(
    CacheBaseConfig::class,
    CachePolicyConfig::class,
    CacheImplConfig::class,
    CacheLayerConfig::class,
    CacheCompositeConfig::class,
    CacheSerializerConfig::class,
    CacheManagerConfig::class,
    CacheWarmUpConfig::class,
    CacheAspectConfig::class,
    LockServiceConfig::class
)
@EnableConfigurationProperties(CacheProperties::class)
class CacheAutoConfiguration