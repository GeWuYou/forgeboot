/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.safeguard.redis.codec

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec

/**
 * JacksonPayloadCodec 是一个基于 Jackson 库实现的 PayloadCodec 实现类。
 * 它提供了对象序列化为字节数组和字节数组反序列化为对象的功能。
 *
 * @property mapper 用于序列化和反序列化的 ObjectMapper 实例
 * @since 2025-09-21 13:10:18
 * @author gewuyou
 */
class JacksonPayloadCodec(
    private val mapper: ObjectMapper,
) : PayloadCodec {
    /**
     * 将给定的对象序列化为字节数组。
     *
     * @param value 需要序列化的对象，可以为 null
     * @return 序列化后的字节数组，如果输入为 null 则返回 null
     */
    override fun serialize(value: Any?) =
        value?.let { mapper.writeValueAsBytes(it) }

    /**
     * 将字节数组反序列化为指定类型的对象。
     *
     * @param bytes 包含序列化数据的字节数组，可以为 null
     * @param typeName 目标对象的完整类名，用于指定反序列化的类型，可以为 null
     * @return 反序列化后的对象，如果输入参数为 null 或反序列化失败则返回 null
     */
    override fun deserialize(bytes: ByteArray?, typeName: String?): Any? {
        // 如果输入参数为空，直接返回 null
        if (bytes == null || typeName == null) return null
        // 通过类名获取 Class 对象
        val clazz = Class.forName(typeName)
        // 使用 ObjectMapper 进行反序列化
        return mapper.readValue(bytes, clazz)
    }
}
