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

package com.gewuyou.forgeboot.webmvc.exception.api.shared

import com.gewuyou.forgeboot.i18n.api.InfoLike
import com.gewuyou.forgeboot.webmvc.dto.api.entities.Failure
import com.gewuyou.forgeboot.webmvc.dto.impl.Responses

/**
 * 基本异常类，用于封装系统中的异常信息
 *
 * @param info 异常信息对象，包含异常默认消息和其他相关信息
 * @param cause 异常的根本原因，可为空
 * @param extras 额外的异常信息键值对，用于携带更多上下文数据，默认为空map
 * @since 2025-09-02 14:02:03
 * @author gewuyou
 */
open class BaseException(
    open val info: InfoLike,
    cause: Throwable? = null,
    open val extras: Map<String, Any?> = emptyMap(),
) : RuntimeException(info.defaultMessage, cause) {

    /**
     * 将当前异常转换为失败响应对象
     *
     * @return Failure 失败响应对象，包含异常信息和上下文数据
     */
    open fun toFailure(): Failure {
        return Responses.fail(info, this, extras)
    }
}
