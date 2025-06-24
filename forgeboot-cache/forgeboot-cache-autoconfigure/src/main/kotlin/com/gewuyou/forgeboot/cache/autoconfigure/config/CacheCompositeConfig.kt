package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.entities.CacheLayer
import com.gewuyou.forgeboot.cache.impl.contract.CompositeCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * 缓存复合配置类
 *
 * 该配置类用于定义与缓存相关的主缓存 Bean，通过组合多层缓存实现灵活的缓存策略。
 * 主要职责是创建并初始化 CompositeCache 实例作为 Spring 容器中的核心缓存组件。
 *
 * @since 2025-06-21 11:40:10
 * @author gewuyou
 */
@Configuration
class CacheCompositeConfig {

    /**
     * 创建主缓存 Bean，使用 CompositeCache 组合多层缓存
     *
     * 该方法将 cacheLayers 和 cacheProperties 注入到 CompositeCache 实例中，
     * 构建出一个支持多层缓存结构的主缓存组件。该组件会被 Spring 标记为首选 Bean。
     *
     * @param cacheLayers 缓存层列表，包含多个 CacheLayer 实例，用于构建缓存层级结构
     * @param cacheProperties 缓存配置对象，包含全局缓存相关属性设置
     * @return 返回构建完成的 Cache 实例，实际类型为 CompositeCache
     */
    @Bean("compositeCache")
    @Primary
    fun compositeCache(
        cacheLayers: List<CacheLayer>,
        cacheProperties: CacheProperties,
    ): Cache {
        return CompositeCache(cacheLayers, cacheProperties)
    }
}