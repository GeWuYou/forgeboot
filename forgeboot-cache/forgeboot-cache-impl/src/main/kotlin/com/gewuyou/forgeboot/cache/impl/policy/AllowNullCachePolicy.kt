package com.gewuyou.forgeboot.cache.impl.policy

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy

/**
 * 允许 Null 缓存策略
 *
 * 该策略允许缓存 null 值，并提供相应的占位符机制。
 *
 * @since 2025-06-20 21:42:24
 * @author gewuyou
 */
class AllowNullCachePolicy(
    private val cacheProperties: CacheProperties
) : NullValuePolicy {

    /**
     * 判断是否允许缓存 null 值。
     *
     * @param key 缓存项的键
     * @return Boolean 表示是否允许缓存 null 值，始终返回 true
     */
    override fun allowCacheNull(key: String): Boolean {
        // 始终允许缓存 null 值
        return true
    }

    /**
     * 获取表示 null 值的占位符。
     *
     * @return String 表示 null 值的占位符
     */
    override fun nullPlaceholder(): String {
        // 定义一个占位符，表示 null 值
        return cacheProperties.nullValuePlaceholder
    }

    /**
     * 判断给定值是否为 null 占位符。
     *
     * @param value 要检查的值
     * @return Boolean 表示给定值是否为 null 占位符
     */
    override fun isNullPlaceholder(value: String): Boolean {
        // 判断值是否为 null 占位符
        return value == cacheProperties.nullValuePlaceholder
    }
}
