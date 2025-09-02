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
import com.gewuyou.forgeboot.i18n.api.InfoResolver
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.api.config.ResponseProps
import com.gewuyou.forgeboot.webmvc.dto.api.entities.*
import org.springframework.core.env.AbstractEnvironment
import org.springframework.http.HttpStatus

/**
 * 默认API响应实现类，用于构建统一格式的成功或失败响应。
 *
 * @property infoResolver 信息解析器，用于解析响应中的信息对象。
 * @property requestIdProvider 请求ID提供者，用于获取当前请求的唯一标识。
 * @property contributors 额外贡献者列表，用于动态添加额外响应字段。
 * @property props 响应配置属性，控制响应行为如是否包含请求ID、错误详情等。
 * @since 2025-09-02 13:11:30
 * @author gewuyou
 */
class DefaultApiResponses(
    private val infoResolver: InfoResolver,
    private val requestIdProvider: RequestIdProvider,
    private val contributors: List<ExtraContributor>,
    private val props: ResponseProps,
) : ApiResponses {

    /**
     * 处理并返回附加信息映射表。
     *
     * 如果启用了自动附加信息（[ResponseProps.autoExtras] 为 true），
     * 则会调用所有注册的 [ExtraContributor] 来填充附加信息。
     *
     * @param extras 初始附加信息映射表。
     * @return 合并后的附加信息映射表。
     */
    private fun extras(extras: Map<String, Any?>): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>().apply { putAll(extras) }
        if (props.autoExtras) contributors.forEach { it.contribute(map) }
        return map
    }

    /**
     * 获取请求ID，如果配置中启用了请求ID包含功能。
     *
     * @return 请求ID字符串，如果未启用则返回 null。
     */
    private fun reqIdOrNull(): String? =
        if (props.includeRequestId) requestIdProvider.getRequestId() else null

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
    ): SuccessMessage {
        val resolved = infoResolver.resolve(
            object : InfoLike {
                override val code: Int = HttpStatus.OK.value()

                /**
                 * 消息键，用于国际化支持
                 *
                 * 例如: "ok", "error.validation_failed"
                 * 该键用于从资源文件中获取对应语言的消息文本
                 */
                override val messageKey = null

                /**
                 * 消息参数，用于动态替换消息中的占位符
                 *
                 * 例如: ["张三", "18"]
                 * 在消息文本中，可以使用{}来表示占位符，例如"Hello, {}! You are {} years old."
                 * 当调用时传入参数["张三", "18"]，将替换成"Hello, 张三! You are 18 years old."
                 */
                override val messageArgs = null
                override val defaultMessage: String
                    get() = message
            },
        )
        return SuccessMessage(
            code = resolved.code,
            message = resolved.message,
            requestId = reqIdOrNull(),
            extra = extras(extra)
        )
    }

    /**
     * 构建一个表示操作成功响应对象。
     *
     * @param T 数据类型。
     * @param data 成功时返回的数据。
     * @param info 响应信息描述对象。
     * @param extra 附加信息映射表。
     * @return 表示成功响应对象。
     */
    override fun <T> ok(
        info: InfoLike,
        data: T,
        extra: Map<String, Any?>,
    ): Success<T> {
        val resolved = infoResolver.resolve(info)
        return Success(
            code = resolved.code,
            message = resolved.message,
            data = data,
            requestId = reqIdOrNull(),
            extra = extras(extra)
        )
    }

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
    ): SuccessMessage {
        return SuccessMessage(
            code = info.code,
            message = info.defaultMessage,
            requestId = reqIdOrNull(),
            extra = extras(extra)
        )
    }

    /**
     * 构建一个表示操作失败的响应对象。
     *
     * 根据配置决定是否暴露错误详情：
     * - "always": 总是暴露；
     * - "on_debug": 仅在调试模式下暴露；
     * - 其他情况不暴露。
     *
     * @param info 响应信息描述对象。
     * @param error 错误对象，可能被暴露为详细错误信息。
     * @param extra 附加信息映射表。
     * @return 表示失败的响应对象。
     */
    override fun fail(
        info: InfoLike,
        error: Any?,
        extra: Map<String, Any?>,
    ): Failure {
        val resolved = infoResolver.resolve(info)
        val detail = when (props.exposeErrorDetail) {
            "always" -> error
            "on_debug" -> if (isDebug()) error else null
            else -> null
        }
        return Failure(
            code = resolved.code,
            message = resolved.message,
            error = detail,
            requestId = reqIdOrNull(),
            extra = extras(extra)
        )
    }

    /**
     * 判断当前是否处于调试模式。
     *
     * 通过检查系统属性中是否包含 "dev" profile 来判断。
     *
     * @return 如果处于调试模式返回 true，否则返回 false。
     */
    private fun isDebug(): Boolean =
        AbstractEnvironment
            .ACTIVE_PROFILES_PROPERTY_NAME
            .let { System.getProperty(it)?.contains("dev") == true }
}
