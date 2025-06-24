package com.gewuyou.forgeboot.cache.impl.support

import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator
import com.gewuyou.forgeboot.cache.impl.utils.SpELResolver
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature

/**
 * 缓存 SpEL 表达式帮助类
 * 提供基于方法参数的 SpEL 解析和缓存键生成能力
 *
 * @since 2025-06-18 21:04:03
 * @author gewuyou
 */
object CacheSpELHelper {

    /**
     * 构建方法参数名称到参数值的映射关系
     * 用于支持 SpEL 表达式解析时的参数上下文
     *
     * @param joinPoint AOP 方法连接点，包含方法签名和参数信息
     * @return 参数名到参数值的映射表
     */
    fun buildArgsMap(joinPoint: JoinPoint): Map<String, Any?> {
        val method = (joinPoint.signature as MethodSignature).method
        // 将方法参数列表转换为（参数名 -> 实际参数值）的键值对集合
        return method.parameters.mapIndexed { i, param -> param.name to joinPoint.args[i] }.toMap()
    }

    /**
     * 解析 SpEL 表达式并生成最终缓存键
     *
     * @param namespace 缓存命名空间，用于隔离不同业务的缓存键
     * @param keySpEL   SpEL 表达式字符串，定义缓存键的动态生成逻辑
     * @param joinPoint AOP 方法连接点，提供运行时参数上下文
     * @param keyGenerator 用户定义的缓存键生成策略
     * @return 最终生成的缓存键字符串
     */
    fun parseKey(
        namespace: String,
        keySpEL: String,
        joinPoint: JoinPoint,
        keyGenerator: KeyGenerator
    ): String {
        val argsMap = buildArgsMap(joinPoint)
        // 使用 SpEL 解析器根据参数上下文解析表达式得到原始键值
        val key = SpELResolver.parse(keySpEL, argsMap)
        // 委托给键生成器结合命名空间生成最终缓存键
        return keyGenerator.generateKey(namespace, key)
    }
}