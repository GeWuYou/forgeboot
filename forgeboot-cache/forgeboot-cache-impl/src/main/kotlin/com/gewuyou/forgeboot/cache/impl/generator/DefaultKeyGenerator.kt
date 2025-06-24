package com.gewuyou.forgeboot.cache.impl.generator

import com.gewuyou.forgeboot.cache.api.generator.KeyGenerator

/**
 * 默认缓存键生成器
 *
 * 该类负责生成带有命名空间的缓存键，通过将命名空间和键值拼接为 `namespace:key` 的形式。
 *
 * @since 2025-06-18 12:40:04
 * @author gewuyou
 */
class DefaultKeyGenerator : KeyGenerator {

    /**
     * 生成缓存键
     *
     * 将给定的命名空间和键拼接成一个完整的缓存键，格式为 `namespace:key`。
     *
     * @param namespace 命名空间，用于隔离不同的缓存区域
     * @param key       缓存项的具体键值
     * @return 拼接后的完整缓存键
     */
    override fun generateKey(namespace: String, key: String): String {
        return "$namespace${this.getConnectors()}$key"
    }

    /**
     * 获取连接符
     *
     * 返回用于拼接命名空间和基础键值的连接符，默认实现可能返回冒号（:）或其他特定字符。
     * 此方法允许子类自定义连接符，以满足不同存储结构或命名规范的需求。
     *
     * @return 返回连接符字符串
     */
    override fun getConnectors(): String {
        return ":"
    }
}