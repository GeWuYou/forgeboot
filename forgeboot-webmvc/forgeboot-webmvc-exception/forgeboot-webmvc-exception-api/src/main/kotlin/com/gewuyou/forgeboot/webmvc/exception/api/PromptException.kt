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

package com.gewuyou.forgeboot.webmvc.exception.api

import com.gewuyou.forgeboot.i18n.api.InfoLike
import com.gewuyou.forgeboot.webmvc.exception.api.shared.BaseException

/**
 * 异常类，用于处理提示信息相关的异常情况
 *
 * @param info 异常信息对象，包含异常的详细描述信息
 * @param cause 异常的根本原因，可为空
 * @param extras 异常的额外信息映射表，默认为空映射
 * @since 2025-09-02 14:02:03
 * @author gewuyou
 */
open class PromptException(
    override val info: InfoLike,
    cause: Throwable? = null,
    override val extras: Map<String, Any?> = emptyMap(),
) : BaseException(
    info,
    cause,
    extras
)
