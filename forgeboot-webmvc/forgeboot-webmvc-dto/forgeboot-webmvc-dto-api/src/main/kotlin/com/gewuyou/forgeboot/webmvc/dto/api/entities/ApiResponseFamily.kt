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

/**
 * API响应的密封接口
 *
 * 定义了API响应的基本结构，包括状态码、消息、请求ID和额外信息。
 * 所有API响应类型都必须实现此接口。
 *
 */
sealed interface ApiResponse {
    /**
     * 状态码
     *
     * 表示请求处理结果的状态码，通常遵循HTTP状态码规范
     */
    val code: Int

    /**
     * 响应消息
     *
     * 描述请求处理结果的消息文本
     */
    val message: String

    /**
     * 请求ID
     *
     * 用于追踪请求的唯一标识符，便于调试和日志追踪
     */
    val requestId: String?

    /**
     * 额外信息
     *
     * 包含其他需要返回的键值对信息
     */
    val extra: Map<String, Any?>
}

/**
 * 成功响应数据类
 *
 * 表示请求处理成功响应，包含具体的业务数据
 *
 * @property code 状态码
 * @property message 响应消息
 * @property data 业务数据
 * @property requestId 请求ID
 * @property extra 额外信息
 * @param T 业务数据的类型参数
 */
data class Success<T>(
    override val code: Int,
    override val message: String,
    val data: T,
    override val requestId: String?,
    override val extra: Map<String, Any?>,
) : ApiResponse

/**
 * SuccessMessage 数据类表示一个成功的API响应消息
 *
 * @param code 响应状态码，通常200表示成功
 * @param message 响应消息文本，描述响应结果
 * @param requestId 请求唯一标识符，用于追踪请求链路，可为空
 * @param extra 额外的响应数据，以键值对形式存储，值可以为空
 */
data class SuccessMessage(
    override val code: Int,
    override val message: String,
    override val requestId: String?,
    override val extra: Map<String, Any?>,
) : ApiResponse

/**
 * 失败响应数据类
 *
 * 表示请求处理失败的响应，包含错误详情信息
 *
 * @property code 状态码
 * @property message 响应消息
 * @property error 错误详情信息
 * @property requestId 请求ID
 * @property extra 额外信息
 */
data class Failure(
    override val code: Int,
    override val message: String,
    val error: Any?,                 // 可承载错误细节（字段错误列表、异常码等）
    override val requestId: String?,
    override val extra: Map<String, Any?>,
) : ApiResponse