package com.gewuyou.forgeboot.context.impl

import com.gewuyou.forgeboot.context.api.AbstractContext
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer

/**
 * 上下文容器，用于存储和获取键值对形式的字符串数据。
 *
 * @since 2025-06-04 15:05:43
 * @author gewuyou
 */
open class ContextHolder(
    private val valueSerializer: ValueSerializer
) : AbstractContext<String, String>() {

    /**
     * 存储键值对到上下文中。如果值为 null，则不会存储。
     *
     * @param key   要存储的键
     * @param value 要存储的值，可以为 null
     */
    open fun put(key: String, value: Any?) {
        super.put(key, value?.let { valueSerializer.serialize(it) })
    }

    /**
     * 根据指定的键和类型从上下文中获取对应的值。
     *
     * @param key  要查找的键
     * @param type 要转换的目标类型
     * @return 对应类型的值，如果不存在或类型不匹配则返回 null
     */
    override fun <T> retrieveByType(key: String, type: Class<T>): T? {
        return retrieve(key)?.let { valueSerializer.deserialize(it, type) }
    }
    /**
     * 将指定的映射中的所有键值对存储到上下文中。
     *
     * @param map 包含键值对的映射，键为字符串类型，值可以为任意类型或 null
     */
    fun putAll(map: Map<String, Any?>) {
        map.forEach { (k, v) -> put(k, v) }
    }
}