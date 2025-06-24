package com.gewuyou.forgeboot.cache.api.policy

/**
 * 缓存策略接口，定义了缓存数据应用的规则。
 *
 * 实现该接口的类应提供一个 apply 方法，用于根据给定的键和值来执行特定的缓存策略。
 * 例如，可以在此方法中实现过期时间设置、缓存淘汰算法等逻辑。
 *
 * @since 2025-06-21 11:54:50
 * @author gewuyou
 */
interface CachePolicy {
    /**
     * 根据给定的键和值来执行缓存策略。
     *
     * @param key   缓存项的键，用于唯一标识一个缓存条目
     * @param value 可为空的字符串，表示与键关联的值；为 null 时可能指示移除或跳过缓存操作
     * @return      返回处理后的缓存值，也可以是 null 表示不进行缓存
     */
    fun apply(key: String, value: String?): String?

    /**
     * 获取该策略的优先级，用于决定多个策略的执行顺序
     *
     * @return 优先级，值越小表示优先级越高
     */
    fun getOrder(): Int
}