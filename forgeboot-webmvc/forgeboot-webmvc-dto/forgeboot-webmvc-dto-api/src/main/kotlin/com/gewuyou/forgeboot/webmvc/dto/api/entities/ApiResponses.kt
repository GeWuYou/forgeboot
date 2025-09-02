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
import com.gewuyou.forgeboot.webmvc.dto.api.Infos

/**
 * API响应工厂接口
 *
 * 用于创建标准化的API响应对象，包括成功响应和错误响应。
 * 提供统一的方法来构建符合规范的API响应。
 *
 * @since 2025-09-02 12:31:13
 * @author gewuyou
 */
interface ApiResponses {
    /**
     * 创建一个成功的消息对象
     *
     * @param message 消息内容
     * @param extra 包含额外信息的键值对映射
     * @return SuccessMessage 成功消息对象
     */
    fun ok(
        message: String,
        extra: Map<String, Any?> = emptyMap(),
    ): SuccessMessage

    /**
     * 创建成功响应对象
     *
     * 根据提供的数据和信息构建一个成功响应对象。
     *
     * @param data 业务数据
     * @param T 数据类型参数
     * @param info 包含状态码和消息键的信息对象，默认为OK信息
     * @param extra 额外的键值对信息，默认为空Map
     * @return Success<T> 成功响应对象
     */
    fun <T> ok(
        info: InfoLike = Infos.OK,
        data: T,
        extra: Map<String, Any?> = emptyMap(),
    ): Success<T>

    /**
     * 创建一个成功的消息对象
     *
     * @param info 包含成功信息的对象，实现InfoLike接口
     * @param extra 包含额外信息的键值对映射
     * @return SuccessMessage 成功消息对象
     */
    fun ok(
        info: InfoLike,
        extra: Map<String, Any?>,
    ): SuccessMessage

    /**
     * 创建错误响应对象
     *
     * 根据提供的错误信息构建一个错误响应对象。
     *
     * @param info 包含状态码和消息键的信息对象，默认为BAD_REQUEST信息
     * @param error 错误详情信息，默认为null
     * @param extra 额外的键值对信息，默认为空Map
     * @return Failure 错误响应对象
     */
    fun fail(
        info: InfoLike = Infos.BAD_REQUEST,
        error: Any? = null,
        extra: Map<String, Any?> = emptyMap(),
    ): Failure
}
