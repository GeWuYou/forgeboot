package com.gewuyou.forgeboot.core.serialization.serializer.impl.serializer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.core.serialization.exception.SerializerException
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer


/**
 * Jackson 值序列化器，用于将对象序列化为 JSON 字符串或将 JSON 字符串反序列化为对象。
 *
 * @param mapper 用于执行序列化和反序列化的 Jackson ObjectMapper 实例
 * @since 2025-06-18 12:37:59
 * @author gewuyou
 */
class JacksonValueSerializer(
    private val mapper: ObjectMapper,
) : ValueSerializer {
    /**
     * 将给定的对象序列化为字符串。
     *
     * @param value 要序列化的对象
     * @return 序列化后的字符串表示形式
     */
    override fun serialize(value: Any): String {
        // 对于基本类型或者 String，直接返回
        if (value is String || value.javaClass.isPrimitive) {
            return value.toString()
        }
        return try {
            mapper.writeValueAsString(value)
        } catch (e: JsonProcessingException) {
            throw SerializerException("Failed to serialize value: ${e.message}", e)
        }
    }

    /**
     * 将给定的字符串反序列化为对象。
     *
     * @param value 要反序列化的字符串
     * @return 反序列化后的对象
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> deserialize(value: String, type: Class<T>): T {
        // 对于 String 类型，直接返回
        if (type == String::class.java) {
            return value as T
        }
        // 对于基本类型，使用相应的 valueOf 方法进行转换
        if (type.isPrimitive) {
            return when (type) {
                Boolean::class.java -> value.toBoolean() as T
                Integer::class.java, Int::class.java -> value.toInt() as T
                Long::class.java, java.lang.Long::class.java -> value.toLong() as T
                Double::class.java, java.lang.Double::class.java -> value.toDouble() as T
                Float::class.java, java.lang.Float::class.java -> value.toFloat() as T
                Short::class.java, java.lang.Short::class.java -> value.toShort() as T
                Byte::class.java, java.lang.Byte::class.java -> value.toByte() as T
                Character::class.java -> value[0] as T // 取第一个字符作为字符
                else -> throw SerializerException("Unsupported primitive type: ${type.name}")
            }
        }
        return try {
            mapper.readValue(value, type)
        } catch (e: JsonProcessingException) {
            throw SerializerException("Failed to deserialize value: ${e.message}", e)
        }
    }

}