package com.gewuyou.forgeboot.common.result

import com.gewuyou.forgeboot.common.result.api.MessageResolver
import com.gewuyou.forgeboot.common.result.api.RequestIdProvider
import com.gewuyou.forgeboot.common.result.api.ResponseInformation
import com.gewuyou.forgeboot.common.result.api.ResultExtender
import com.gewuyou.forgeboot.common.result.impl.DefaultMessageResolver
import com.gewuyou.forgeboot.common.result.impl.DefaultRequestIdProvider

/**
 * 统一响应封装类
 *
 * 该类用于统一系统中的响应格式，提供基本地响应信息如状态码、是否成功、消息内容、数据体等
 * 并支持通过扩展器[ResultExtender]来扩展响应信息
 *
 * @param code 状态码
 * @param success 是否成功
 * @param message 响应消息
 * @param data 响应数据体
 * @param requestId 请求ID
 * @param nameMap 响应字段名称映射表
 *
 * @since 2025-05-03 16:04:42
 * @author gewuyou
 */
data class R<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val requestId: String? = null,
    val nameMap: Map<String, String> = mapOf(
        "code" to "code",
        "success" to "success",
        "message" to "message",
        "data" to "data"
    )
) {
    /**
     * 将响应对象转换为Map对象
     *
     * 此方法用于将响应对象转换为Map，方便在不同层次中传递和处理响应信息
     * 同时，它允许通过[ResultExtender]来进一步扩展响应信息
     *
     * @param extenders 结果扩展器列表，默认为空
     * @return 转换后的Map对象
     */
    fun toMap(extenders: List<ResultExtender> = emptyList()): MutableMap<String, Any?> {
        val r = mutableMapOf(
            "code" to code,
            "success" to success,
            "message" to message,
            "data" to data
        )
        if (!requestId.isNullOrBlank()) {
            r["requestId"] = requestId
        }
        extenders.forEach { it.extend(r) }
        return r
    }

    companion object {

        /**
         * 创建成功地响应对象
         *
         * 该方法用于根据[ResponseInformation]创建一个成功地响应对象
         * 它支持国际化消息解析和请求ID的生成，并允许通过[ResultExtender]来扩展响应信息
         *
         * @param info 响应信息对象
         * @param data 响应数据体，默认为null
         * @param messageResolver 消息解析器，默认为[DefaultMessageResolver]
         * @param requestIdProvider 请求ID提供者，默认为[DefaultRequestIdProvider]
         * @param extenders 结果扩展器列表，默认为空
         * @return 创建的响应对象
         */
        fun <T> success(
            info: ResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            i18bArgs: Array<Any>? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(
                info.responseI8nMessageCode,
                i18bArgs
            )
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            return R(info.responseCode, true, msg, data, reqId).also {
                extenders.forEach { extender -> extender.extend(it.toMap()) }
            }
        }

        /**
         * 创建失败的响应对象
         *
         * 该方法用于根据[ResponseInformation]创建一个失败的响应对象
         * 它支持国际化消息解析和请求ID的生成，并允许通过[ResultExtender]来扩展响应信息
         *
         * @param info 响应信息对象
         * @param data 响应数据体，默认为null
         * @param messageResolver 消息解析器，默认为[DefaultMessageResolver]
         * @param requestIdProvider 请求ID提供者，默认为[DefaultRequestIdProvider]
         * @param extenders 结果扩展器列表，默认为空
         * @return 创建的响应对象
         */
        fun <T> failure(
            info: ResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            i18bArgs: Array<Any>? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(
                info.responseI8nMessageCode,
                i18bArgs
            )
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            return R(info.responseCode, false, msg, data, reqId).also {
                extenders.forEach { extender -> extender.extend(it.toMap()) }
            }
        }

        /**
         * 创建成功地响应对象
         *
         * 该方法用于创建一个简单的成功响应对象，支持自定义状态码、消息代码、消息参数和数据体
         * 它支持国际化消息解析和请求ID的生成，并允许通过[ResultExtender]来扩展响应信息
         *
         * @param code 状态码，默认为200
         * @param messageCode 消息代码，默认为"success"
         * @param args 消息参数，默认为null
         * @param data 响应数据体，默认为null
         * @param messageResolver 消息解析器，默认为[DefaultMessageResolver]
         * @param requestIdProvider 请求ID提供者，默认为[DefaultRequestIdProvider]
         * @param extenders 结果扩展器列表，默认为空
         * @return 创建的响应对象
         */
        fun <T> success(
            code: Int = 200,
            messageCode: String = "success",
            data: T? = null,
            i18bArgs: Array<Any>? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18bArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            return R(code, true, msg, data, reqId).also {
                extenders.forEach { extender -> extender.extend(it.toMap()) }
            }
        }

        /**
         * 创建失败的响应对象
         *
         * 该方法用于创建一个简单的失败响应对象，支持自定义状态码、消息代码、消息参数和数据体
         * 它支持国际化消息解析和请求ID的生成，并允许通过[ResultExtender]来扩展响应信息
         *
         * @param code 状态码，默认为400
         * @param messageCode 消息代码，默认为"error"
         * @param args 消息参数，默认为null
         * @param data 响应数据体，默认为null
         * @param messageResolver 消息解析器，默认为[DefaultMessageResolver]
         * @param requestIdProvider 请求ID提供者，默认为[DefaultRequestIdProvider]
         * @param extenders 结果扩展器列表，默认为空
         * @return 创建的响应对象
         */
        fun <T> failure(
            code: Int = 400,
            messageCode: String = "error",
            data: T? = null,
            i18bArgs: Array<Any>? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18bArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            return R(code, false, msg, data, reqId).also {
                extenders.forEach { extender -> extender.extend(it.toMap()) }
            }
        }

    }
}
