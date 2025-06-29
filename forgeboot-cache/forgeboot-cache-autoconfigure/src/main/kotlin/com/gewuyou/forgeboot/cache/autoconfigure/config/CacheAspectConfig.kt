package com.gewuyou.forgeboot.cache.autoconfigure.config

import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheManager
import com.gewuyou.forgeboot.cache.impl.aspect.CacheEvictExAspect
import com.gewuyou.forgeboot.cache.impl.aspect.CachePutExAspect
import com.gewuyou.forgeboot.cache.impl.aspect.CacheableExAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 缓存切面配置类
 *
 * 用于定义缓存相关的切面 Bean，包括 CacheableExAspect、CacheEvictExAspect 和 CachePutExAspect。
 * 这些切面处理基于 Spring AOP 的缓存操作逻辑。
 *
 * @since 2025-06-21 11:41:51
 * @author gewuyou
 */
@Configuration
class CacheAspectConfig {

    /**
     * 创建 CacheableExAspect 切面 Bean
     *
     * 该方法将 CacheManager 和 KeyGenerator 注入到 CacheableExAspect 实例中，
     * 用于构建支持扩展的缓存获取切面逻辑。
     *
     * @param cacheManager 缓存管理器，用于管理具体的缓存实现
     * @param keyGenerator 键生成器，用于根据方法参数生成缓存键
     * @return 初始化完成的 CacheableExAspect 实例
     */
    @Bean
    fun cacheableExAspect(
        cacheManager: CacheManager,
        keyGenerator: KeyGenerator
    ): CacheableExAspect {
        return CacheableExAspect(cacheManager, keyGenerator)
    }

    /**
     * 创建 CacheEvictExAspect 切面 Bean
     *
     * 该方法将 CacheManager 和 KeyGenerator 注入到 CacheEvictExAspect 实例中，
     * 用于构建支持扩展的缓存清除切面逻辑。
     *
     * @param cacheManager 缓存管理器，用于管理具体的缓存实现
     * @param keyGenerator 键生成器，用于根据方法参数生成缓存键
     * @return 初始化完成的 CacheEvictExAspect 实例
     */
    @Bean
    fun cacheEvictExAspect(
        cacheManager: CacheManager,
        keyGenerator: KeyGenerator
    ): CacheEvictExAspect {
        return CacheEvictExAspect(cacheManager, keyGenerator)
    }

    /**
     * 创建 CachePutExAspect 切面 Bean
     *
     * 该方法将 CacheManager 和 KeyGenerator 注入到 CachePutExAspect 实例中，
     * 用于构建支持扩展的缓存更新切面逻辑。
     *
     * @param cacheManager 缓存管理器，用于管理具体的缓存实现
     * @param keyGenerator 键生成器，用于根据方法参数生成缓存键
     * @return 初始化完成的 CachePutExAspect 实例
     */
    @Bean
    fun cachePutExAspect(
        cacheManager: CacheManager,
        keyGenerator: KeyGenerator
    ): CachePutExAspect {
        return CachePutExAspect(cacheManager, keyGenerator)
    }
}
