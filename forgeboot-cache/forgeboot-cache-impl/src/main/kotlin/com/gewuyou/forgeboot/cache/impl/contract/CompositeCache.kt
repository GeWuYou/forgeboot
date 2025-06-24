package com.gewuyou.forgeboot.cache.impl.contract

import com.gewuyou.forgeboot.cache.api.config.CacheProperties
import com.gewuyou.forgeboot.cache.api.contract.Cache
import com.gewuyou.forgeboot.cache.api.entities.CacheLayer
import java.time.Duration

/**
 * 复合缓存实现类，通过组合多个缓存层提供统一的缓存访问接口。
 *
 * 缓存按照优先级排序，高优先级的缓存层在前。读取时会从高到低依次查找，
 * 一旦在某一层找到数据，就会将该数据回填到优先级更高的缓存层中。
 *
 * @property layers 缓存层列表，每个缓存层包含一个具体的缓存实例和优先级。
 * @property cacheProperties 配置属性对象，用于获取默认缓存过期时间等配置参数。
 * @constructor 创建一个复合缓存实例，并按优先级对缓存层进行排序。
 */
class CompositeCache(
    layers: List<CacheLayer>,
    private val cacheProperties: CacheProperties
) : Cache {

    /**
     * 按照优先级排序后的缓存层列表。
     * 高优先级的缓存层排在前面，用于后续的缓存查找与回填逻辑。
     */
    private val sortedLayers = layers.sortedBy { it.priority }

    /**
     * 获取指定键对应的缓存值。
     *
     * 从最高优先级的缓存层开始查找，一旦找到有效值，则将其回填到
     * 所有优先级高于当前层的缓存中，并返回该值。
     *
     * @param key 要获取的缓存键。
     * @return 如果存在缓存值则返回对应值，否则返回 null。
     */
    override fun retrieve(key: String): String? {
        for ((index, layer) in sortedLayers.withIndex()) {
            val value = layer.cache.retrieve(key)
            if (value != null) {
                // 将找到的值回填到所有更高优先级的缓存层中
                for (i in 0 until index) {
                    sortedLayers[i].cache[key, value] = cacheProperties.theDefaultCacheTTL
                }
                return value
            }
        }
        return null
    }

    /**
     * 将指定键值对放入所有缓存层中。
     *
     * @param key 要存储的缓存键。
     * @param value 要存储的缓存值。
     * @param ttl 存活时间。
     */
    override fun put(key: String, value: String?, ttl: Duration) {
        sortedLayers.forEach { it.cache[key, value] = ttl }
    }

    /**
     * 从所有缓存层中移除指定键的缓存项。
     *
     * @param key 要移除的缓存键。
     * @return 如果所有缓存层都成功移除了该键则返回 true，否则返回 false。
     */
    override fun remove(key: String): Boolean {
        return sortedLayers.all { it.cache.remove(key) }
    }

    /**
     * 判断指定键是否存在于任意一个缓存层中。
     *
     * @param key 要检查的缓存键。
     * @return 如果存在任意一层缓存包含该键则返回 true，否则返回 false。
     */
    override fun exists(key: String): Boolean {
        return sortedLayers.any { it.cache.exists(key) }
    }

    /**
     * 清空缓存中的所有数据。
     *
     * @param namespace 命名空间，用于限定清空操作的作用域。
     */
    override fun clear(namespace: String) {
        sortedLayers.forEach { it.cache.clear(namespace) }
    }
}