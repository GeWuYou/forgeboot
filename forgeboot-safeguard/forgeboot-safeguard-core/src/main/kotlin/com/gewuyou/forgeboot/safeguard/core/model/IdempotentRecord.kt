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

package com.gewuyou.forgeboot.safeguard.core.model

import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus

/**
 * 幂等性记录数据类，用于存储幂等性相关的信息
 *
 * @property status 幂等性状态
 * @property payloadType 负载数据类型，可为空
 * @property payload 负载数据字节数组，可为空
 * @since 2025-09-21 11:11:49
 * @author gewuyou
 */
data class IdempotentRecord(
    val status: IdempotencyStatus,
    val payloadType: String?,
    val payload: ByteArray?,
) {
    /**
     * 比较两个IdempotencyRecord对象是否相等
     * 逐个比较status、payloadType和payload属性
     *
     * @param other 要比较的对象
     * @return 如果对象相等返回true，否则返回false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdempotentRecord) return false

        if (status != other.status) return false
        if (payloadType != other.payloadType) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    /**
     * 计算对象的哈希码
     * 基于status、payloadType和payload属性计算哈希值
     *
     * @return 对象的哈希码
     */
    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (payloadType?.hashCode() ?: 0)
        result = 31 * result + (payload?.contentHashCode() ?: 0)
        return result
    }
}
