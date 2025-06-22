package com.gewuyou.forgeboot.core.serialization.serializer

/**
 * 值序列化器接口，用于定义将对象序列化为字符串以及从字符串反序列化为对象的方法。
 *
 * @since 2025-06-16 22:16:19
 * @author gewuyou
 */
interface ValueSerializer {

    /**
     * 将给定的对象序列化为字符串。
     *
     * @param value 要序列化的对象
     * @return 序列化后的字符串表示形式
     */
    fun serialize(value: Any): String

    /**
     * 将给定的字符串反序列化为对象。
     *
     * @param value 要反序列化的字符串
     * @return 反序列化后的对象
     */
    fun <T> deserialize(value: String, type: Class<T>): T
}