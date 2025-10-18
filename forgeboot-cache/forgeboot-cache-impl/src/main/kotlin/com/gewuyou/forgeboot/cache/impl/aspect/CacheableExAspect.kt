/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.cache.impl.aspect

import com.gewuyou.forgeboot.cache.api.annotations.CacheableEx
import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.api.manager.CacheServiceManager
import com.gewuyou.forgeboot.cache.impl.utils.SpELResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import java.time.Duration

/**
 * 可缓存的扩展切面类
 *
 * 用于处理带有 @CacheableEx 注解的方法，实现方法结果的缓存逻辑。
 * 通过 AOP 拦截注解方法，根据配置生成缓存键并操作缓存服务。
 *
 * @property cacheServiceManager 缓存管理器，用于操作缓存服务
 * @property keyGenerator 缓存键生成器，用于生成完整的缓存键
 */
@Aspect
class CacheableExAspect(
    private val cacheServiceManager: CacheServiceManager,
    private val keyGenerator: KeyGenerator,
) {

    /**
     * 环绕通知方法，处理缓存逻辑
     *
     * 执行流程：
     * 1. 解析方法参数并生成参数映射表
     * 2. 使用 SpEL 表达式解析器生成缓存键
     * 3. 从缓存中尝试获取已存在的值
     * 4. 如果缓存命中且值不为 null（或允许缓存 null 值），则直接返回缓存结果
     * 5. 否则执行目标方法，并将结果写入缓存
     *
     * @param joinPoint 切点信息，包含目标方法及参数等
     * @param cacheableEx 方法上的 @CacheableEx 注解实例，提供缓存配置
     * @return 目标方法的执行结果，可能是缓存值也可能是实际调用结果
     */
    @Around("@annotation(cacheableEx)")
    fun handle(joinPoint: ProceedingJoinPoint, cacheableEx: CacheableEx): Any? {
        // 提取目标方法对象
        val method = (joinPoint.signature as MethodSignature).method

        // 构建参数名称与值的映射关系
        val argsMap = method.parameters.mapIndexed { i, param -> param.name to joinPoint.args[i] }.toMap()

        // 使用 SpEL 解析表达式生成缓存键
        val key = SpELResolver.parse(cacheableEx.keySpEL, argsMap)

        // 获取缓存值类型、命名空间和过期时间
        val type = cacheableEx.type.java
        val namespace = cacheableEx.namespace
        val ttl = cacheableEx.ttl

        // 生成完整缓存键
        val fullKey = keyGenerator.generateKey(namespace, key)
        val cacheService = cacheServiceManager.getCache(namespace)
        // 尝试从缓存获取数据
        val cached = cacheService.retrieve(fullKey, type)

        if (cached != null) return cached

        // 执行原始方法逻辑
        val result = joinPoint.proceed()

        // 根据是否允许缓存 null 值进行判断
        if (result == null && !cacheableEx.cacheNull) return null

        // 将非 null 结果写入缓存，空值使用占位符 ""
        cacheService.put(fullKey, result ?: "", Duration.ofSeconds(ttl))
        return result
    }
}