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

package com.gewuyou.forgeboot.webmvc.dto.api.entities

import com.gewuyou.forgeboot.i18n.api.InfoLike

/**
 * 简单信息实现类，用于创建包含状态码、消息键和默认消息的信息对象
 *
 * @property code 状态码
 * @property messageKey 消息键，用于国际化支持
 * @property defaultMessage 默认消息，当找不到对应国际化资源时使用的兜底文案
 */
data class SimpleInfo(
    override val code: Int,
    override val defaultMessage: String,
    override val messageKey: String? = null,
    override val messageArgs: Array<out Any>? = null,
) : InfoLike {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleInfo) return false

        if (code != other.code) return false
        if (messageKey != other.messageKey) return false
        if (defaultMessage != other.defaultMessage) return false
        if (!messageArgs.contentEquals(other.messageArgs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + messageKey.hashCode()
        result = 31 * result + defaultMessage.hashCode()
        result = 31 * result + (messageArgs?.contentHashCode() ?: 0)
        return result
    }
}