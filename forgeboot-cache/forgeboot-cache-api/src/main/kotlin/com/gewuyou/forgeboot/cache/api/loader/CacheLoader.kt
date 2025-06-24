package com.gewuyou.forgeboot.cache.api.loader


/**
 * 通用缓存加载器接口，支持缓存未命中时从源头加载数据。
 *
 * @since 2025-06-16
 */
fun interface CacheLoader<T : Any> {
    /**
     * 加载与指定缓存键关联的数据。
     *
     * @param key 缓存键（字符串形式）
     * @param type 缓存值类型（用于反序列化）
     * @return 加载到的数据，若无则返回 null
     */
    fun load(key: String, type: Class<T>): T?
}
