package com.gewuyou.forgeboot.cache.api.entities

import com.gewuyou.forgeboot.cache.api.contract.Cache

/**
 * 缓存层实体类，用于定义缓存层级结构。
 * 
 * @property cache 关联的缓存实例，用于实际执行缓存操作。
 * @property priority 缓存优先级，用于决定多个缓存实现中的调用顺序。
 *
 * @since 2025-06-16 22:43:15
 * @author gewuyou
 */
data class CacheLayer(
    val cache: Cache,
    val priority: Int
)
