package com.gewuyou.forgeboot.cache.impl.policy

import com.gewuyou.forgeboot.cache.api.policy.CachePolicy
import com.gewuyou.forgeboot.cache.api.policy.NullValuePolicy

/**
 * 空值缓存策略
 * 用于处理缓存中空值（null）的存储和占位逻辑
 *
 * @property nullValuePolicy 提供空值处理策略的接口实现
 * @constructor 创建一个 NullValueCachePolicy 实例
 * @param nullValuePolicy 指定的空值处理策略对象，不可为 null
 * @since 2025-06-21 11:56:54
 * @author gewuyou
 */
class NullValueCachePolicy(
    private val nullValuePolicy: NullValuePolicy
) : CachePolicy {

    /**
     * 应用当前策略对指定的缓存值进行处理
     *
     * @param key 缓存键，用于判断是否允许缓存空值
     * @param value 需要处理的缓存值，可能为 null
     * @return 处理后的缓存值：
     *         - 如果值为 null 且允许缓存，则返回 null 占位符
     *         - 如果值为 null 但不允许缓存，则返回 null
     *         - 否则返回原始非 null 值
     */
    override fun apply(key: String, value: String?): String? {
        return when {
            // 当值为空且策略允许缓存空值时，返回占位符
            value == null && nullValuePolicy.allowCacheNull(key) -> nullValuePolicy.nullPlaceholder()
            // 当值为空但策略不允许缓存时，直接返回 null
            value == null -> null
            // 非空值直接返回
            else -> value
        }
    }

    /**
     * 获取该策略的优先级，用于决定多个策略的执行顺序
     *
     * @return 优先级，值越小表示优先级越高
     */
    override fun getOrder(): Int {
        return 1
    }
}