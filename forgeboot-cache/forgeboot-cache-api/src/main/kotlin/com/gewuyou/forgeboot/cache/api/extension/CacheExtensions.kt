package com.gewuyou.forgeboot.cache.api.extension

import com.gewuyou.forgeboot.cache.api.contract.Cache
import java.time.Duration

/**
 * 重载 set 操作符，用于通过 [] 语法将指定键值对及存活时间存入缓存。
 *
 * 此方法封装了缓存的存储操作，并支持指定缓存项的存活时间（TTL）。
 *
 * @param key   要存储的缓存键，类型为 [String]。
 * @param value 要存储的缓存值，类型为 [String?]，允许为 null。
 * @param ttl   存活时间，类型为 [Duration]，表示缓存项的有效期。
 */
operator fun Cache.set(key: String, value: String?, ttl: Duration) {
    this.put(key, value, ttl)
}

/**
 * 获取指定键对应的缓存值。
 *
 * 从最高优先级的缓存层开始查找，一旦找到有效值，则将其回填到
 * 所有优先级高于当前层的缓存中，并返回该值。
 *
 * 此方法封装了缓存的读取逻辑，并负责维护缓存层级间的数据一致性。
 *
 * @param key 要获取的缓存键，类型为 [String]。
 * @return 如果存在缓存值则返回对应值，类型为 [String?]；否则返回 null。
 */
operator fun Cache.get(key: String): String? {
   return this.retrieve(key)
}