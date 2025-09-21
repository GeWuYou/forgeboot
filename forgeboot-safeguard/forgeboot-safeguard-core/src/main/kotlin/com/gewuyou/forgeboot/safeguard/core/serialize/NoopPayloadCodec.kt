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
 * NoopPayloadCodec 是一个空操作的有效载荷编解码器实现
 * 该编解码器不对数据进行任何序列化或反序列化操作，直接返回null
 *
 * @since 2025-09-21 11:21:23
 * @author gewuyou
 */
object NoopPayloadCodec : PayloadCodec {
    /**
     * 序列化方法，不执行任何实际的序列化操作
     *
     * @param value 需要序列化的对象，可以为null
     * @return 始终返回null，不进行实际的序列化处理
     */
    override fun serialize(value: Any?) = null

    /**
     * 反序列化方法，不执行任何实际的反序列化操作
     *
     * @param bytes 需要反序列化的字节数组，可以为null
     * @param typeName 目标类型名称，可以为null
     * @return 始终返回null，不进行实际的反序列化处理
     */
    override fun deserialize(bytes: ByteArray?, typeName: String?) = null
}
