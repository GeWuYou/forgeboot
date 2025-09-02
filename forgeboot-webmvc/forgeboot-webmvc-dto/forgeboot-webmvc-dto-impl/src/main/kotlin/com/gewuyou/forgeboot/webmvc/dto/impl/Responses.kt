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

package com.gewuyou.forgeboot.webmvc.dto.impl

import com.gewuyou.forgeboot.i18n.api.InfoLike
import com.gewuyou.forgeboot.webmvc.dto.api.entities.ApiResponses
import com.gewuyou.forgeboot.webmvc.dto.api.entities.SuccessMessage

/**
 *响应
 *
 * @since 2025-09-02 13:33:01
 * @author gewuyou
 */
object Responses : ApiResponses {
    /**
     * 委托实例，用于实际处理响应逻辑
     * 使用@Volatile注解确保多线程环境下的可见性
     */
    @Volatile
    private var delegate: ApiResponses? = null

    /**
     * 初始化委托实例
     *
     * @param bean ApiResponses实例，用于处理实际的响应逻辑
     */
    fun init(bean: ApiResponses) {
        delegate = bean
    }

    /**
     * 创建一个成功的消息对象
     *
     * @param message 消息内容
     * @param extra 包含额外信息的键值对映射
     * @return SuccessMessage 成功消息对象
     */
    override fun ok(
        message: String,
        extra: Map<String, Any?>,
    ): SuccessMessage = requireNotNull(delegate) { NOT_INIT_MSG }
        .ok(message, extra)


    /**
     * 创建成功响应
     *
     * @param info 响应信息
     * @param data 响应数据
     * @param extra 额外的键值对数据
     * @return 成功响应结果
     * @throws IllegalStateException 如果Responses未初始化
     */
    override fun <T> ok(
        info: InfoLike,
        data: T,
        extra: Map<String, Any?>,
    ) = requireNotNull(delegate) { NOT_INIT_MSG }
        .ok(info, data, extra)

    /**
     * 创建一个成功的消息对象
     *
     * @param info 包含成功信息的对象，实现InfoLike接口
     * @param extra 包含额外信息的键值对映射
     * @return SuccessMessage 成功消息对象
     */
    override fun ok(
        info: InfoLike,
        extra: Map<String, Any?>,
    ): SuccessMessage = requireNotNull(delegate) { NOT_INIT_MSG }
        .ok(info, extra)

    /**
     * 创建失败响应
     *
     * @param info 响应信息
     * @param error 错误信息
     * @param extra 额外的键值对数据
     * @return 失败响应结果
     * @throws IllegalStateException 如果Responses未初始化
     */
    override fun fail(
        info: InfoLike,
        error: Any?,
        extra: Map<String, Any?>,
    ) = requireNotNull(delegate) { NOT_INIT_MSG }
        .fail(info, error, extra)

    /**
     * 未初始化错误消息常量
     */
    private const val NOT_INIT_MSG =
        "Responses is not initialized. Check your auto-configuration."
}

