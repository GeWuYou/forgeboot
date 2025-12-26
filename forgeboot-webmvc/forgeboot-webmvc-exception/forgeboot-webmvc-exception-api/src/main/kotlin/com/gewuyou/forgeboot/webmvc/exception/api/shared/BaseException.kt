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
import com.gewuyou.forgeboot.webmvc.exception.api.enums.ExceptionLogPolicy

/**
 * 基本异常类，用于封装系统中的异常信息
 *
 * @param info 异常信息对象，包含异常默认消息和其他相关信息
 * @param cause 异常的根本原因，可为空
 * @param extras 额外的异常信息键值对，用于携带更多上下文数据，默认为空map
 * @param logPolicy 异常日志记录策略，默认为BRIEF_LOCATION
 * @since 2025-09-02 14:02:03
 * @author gewuyou
 */
open class BaseException(
    open val info: InfoLike,
    cause: Throwable? = null,
    open val extras: Map<String, Any?> = emptyMap(),
    open val logPolicy: ExceptionLogPolicy = ExceptionLogPolicy.BRIEF_LOCATION
) : RuntimeException(info.defaultMessage, cause) {

    /**
     * 将当前异常转换为失败响应对象
     *
     * @return Failure 失败响应对象，包含异常信息和上下文数据
     */
    open fun toFailure(): Failure {
        return Responses.fail(info, this, extras)
    }

    /**
     * 提供"业务首行栈"，用于 BRIEF_LOCATION
     *
     * @return StackTraceElement? 业务相关的第一个堆栈元素，如果找不到则返回null
     */
    open fun businessStack(): StackTraceElement? {
        // 过滤掉Spring框架、Java标准库、Jakarta库以及当前异常类的堆栈信息，只保留业务代码的堆栈
        return stackTrace.firstOrNull {
            !it.className.startsWith("org.springframework") &&
                    !it.className.startsWith("java.") &&
                    !it.className.startsWith("jakarta.") &&
                    !it.className.contains(this::class.java.name)
        }
    }
}
