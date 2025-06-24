package com.gewuyou.forgeboot.cache.impl.policy

import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy

/**
 * 禁止 Null 缓存策略
 *
 * 该策略实现不允许缓存 null 值的行为，所有与 null 相关的操作都会被禁止并抛出异常。
 *
 * @since 2025-06-20 21:43:00
 * @author gewuyou
 */
class DisallowNullCachePolicy : NullValuePolicy {

    /**
     * 判断是否允许缓存 null 值。
     *
     * @param key 缓存键，用于标识缓存项
     * @return Boolean 返回 false 表示始终不允许缓存 null 值
     */
    override fun allowCacheNull(key: String): Boolean {
        // 始终不允许缓存 null 值
        return false
    }

    /**
     * 获取 null 值的占位符。
     *
     * 由于不允许缓存 null 值，调用此方法会抛出 UnsupportedOperationException。
     *
     * @return String 不会返回任何值，总是抛出异常
     * @throws UnsupportedOperationException 因为不允许缓存 null 值
     */
    override fun nullPlaceholder(): String {
        // 该策略不会使用占位符，因为不允许缓存 null
        throw UnsupportedOperationException("Cannot use null placeholder when null is not allowed in cache.")
    }

    /**
     * 判断指定值是否是 null 占位符。
     *
     * 由于不允许缓存 null 值，此方法无法执行有效判断并会抛出异常。
     *
     * @param value 要检查的值
     * @return Boolean 不会返回任何值，总是抛出异常
     * @throws UnsupportedOperationException 因为不允许缓存 null 值
     */
    override fun isNullPlaceholder(value: String): Boolean {
        // 无法判断 null 占位符，因为 null 值不被缓存
        throw UnsupportedOperationException("Cannot check null placeholder when null is not allowed in cache.")
    }
}
