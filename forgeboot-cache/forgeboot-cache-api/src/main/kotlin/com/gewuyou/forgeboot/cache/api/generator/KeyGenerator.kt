package com.gewuyou.forgeboot.cache.api.generator

/**
 * 键生成器接口
 *
 * 该接口用于统一管理不同场景下的缓存键生成逻辑，通过命名空间和基础键值组合生成完整键。
 *
 * @since 2025-06-16 22:17:36
 * @author gewuyou
 */
interface KeyGenerator {
    /**
     * 生成缓存键
     *
     * 根据传入的命名空间和基础键值，组合生成一个完整的缓存键。实现类应确保返回的键具有唯一性与可读性。
     *
     * @param namespace 命名空间，用于隔离不同的缓存区域，例如模块名称或业务标识
     * @param key       基础键值，表示具体缓存项标识，通常是动态参数或固定字符串
     * @return 返回生成的完整缓存键，通常格式为 "namespace:key"
     */
    fun generateKey(namespace: String, key: String): String

    /**
     * 获取连接符
     *
     * 返回用于拼接命名空间和基础键值的连接符，默认实现可能返回冒号（:）或其他特定字符。
     * 此方法允许子类自定义连接符，以满足不同存储结构或命名规范的需求。
     *
     * @return 返回连接符字符串
     */
    fun getConnectors(): String
}