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

package com.gewuyou.forgeboot.safeguard.core.serialize

/**
 * 有效负载编解码器
 *
 * @since 2025-09-21 11:18:10
 * @author gewuyou
 */
interface PayloadCodec {
    /**
     * 将对象序列化为字节数组
     *
     * @param value 需要序列化的对象，可以为null
     * @return 序列化后的字节数组，如果输入为null则返回null
     */
    fun serialize(value: Any?): ByteArray?

    /**
     * 将字节数组反序列化为指定类型的对象
     *
     * @param bytes 需要反序列化的字节数组，可以为null
     * @param typeName 目标对象的类型名称，可以为null
     * @return 反序列化后的对象，如果输入字节数组为null则返回null
     */
    fun deserialize(bytes: ByteArray?, typeName: String?): Any?
}
