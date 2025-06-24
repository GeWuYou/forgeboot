package com.gewuyou.forgeboot.cache.impl.aspect

import com.gewuyou.forgeboot.cache.api.annotations.CachePutEx
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheManager
import com.gewuyou.forgeboot.cache.impl.support.CacheSpELHelper
import com.gewuyou.forgeboot.cache.impl.utils.SpELResolver
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import java.time.Duration

/**
 * 缓存插入扩展切面
 *
 * 该切面用于处理带有 [CachePutEx] 注解的方法，将方法执行结果根据指定的命名空间和键存储到缓存中，并设置过期时间。
 *
 * @property cacheManager 缓存管理器，用于获取缓存服务实例。
 * @property keyGenerator 缓存键生成器，用于生成完整的缓存键。
 *
 * @since 2025-06-18 20:57:27
 * @author gewuyou
 */
@Aspect
class CachePutExAspect(
    private val cacheManager: CacheManager,
    private val keyGenerator: KeyGenerator
) {

    /**
     * 处理带有 [CachePutEx] 注解的方法返回后逻辑。
     *
     * 通过 AOP 获取方法参数、注解配置的命名空间、键表达式和过期时间，
     * 使用 [SpELResolver] 解析出最终的缓存键，并调用 [cacheManager] 将结果写入缓存。
     *
     * @param joinPoint 切点信息，包含目标方法及其参数。
     * @param cachePutEx 方法上的 [CachePutEx] 注解实例，定义缓存行为配置。
     * @param result 方法执行后的返回值。
     */
    @AfterReturning("@annotation(cachePutEx)", returning = "result")
    fun handle(joinPoint: JoinPoint, cachePutEx: CachePutEx, result: Any?) {
        if (result == null) return
        val namespace = cachePutEx.namespace
        // 解析缓存键
        val fullKey = CacheSpELHelper.parseKey(
            namespace,
            cachePutEx.keySpEL,
            joinPoint,
            keyGenerator
        )
        // 获取缓存过期时间（单位：秒）
        val ttl = cachePutEx.ttl
        cacheManager.getCache(namespace).put(fullKey, result, Duration.ofSeconds(ttl))
    }
}