package com.gewuyou.forgeboot.cache.impl.aspect

import com.gewuyou.forgeboot.cache.api.annotations.CacheEvictEx
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheManager
import com.gewuyou.forgeboot.cache.impl.support.CacheSpELHelper
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

/**
 * 缓存清理扩展切面
 *
 * 该切面用于处理带有 [CacheEvictEx] 注解的方法，在方法执行后清除指定的缓存项。
 *
 * @property cacheManager 用于管理缓存的组件，提供缓存操作的接口。
 * @property keyGenerator 用于生成缓存键的辅助组件，支持动态键值解析。
 *
 * @since 2025-06-18 20:59:18
 * @author gewuyou
 */
@Aspect
class CacheEvictExAspect(
    private val cacheManager: CacheManager,
    private val keyGenerator: KeyGenerator
) {

    /**
     * 处理被 [CacheEvictEx] 注解标记的方法调用后的缓存清理逻辑。
     *
     * 该方法会在目标方法执行完成后触发，根据注解配置的命名空间和 SpEL 表达式
     * 构建完整的缓存键，并通过 [cacheManager] 移除对应的缓存项。
     *
     * @param joinPoint 封装了目标方法调用上下文的信息，包括方法参数、返回值等。
     * @param cacheEvictEx 注解实例，包含缓存清理所需的配置信息。
     */
    @After("@annotation(cacheEvictEx)")
    fun handle(joinPoint: JoinPoint, cacheEvictEx: CacheEvictEx) {
        val namespace = cacheEvictEx.namespace
        // 解析并构建完整的缓存键
        val fullKey = CacheSpELHelper.parseKey(
            namespace,
            cacheEvictEx.keySpEL,
            joinPoint,
            keyGenerator
        )
        // 执行缓存项移除操作
        cacheManager.getCache(namespace).remove(fullKey)
    }
}