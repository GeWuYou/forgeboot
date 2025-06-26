package com.gewuyou.forgeboot.webmvc.dto

import com.gewuyou.forgeboot.trace.api.RequestIdProvider

// 默认成功响应信息
val defaultOkResponseInformation = object : ResponseInformation {
    /**
     * 响应状态码，用于表示响应的状态
     */
    override fun responseStateCode(): Int {
        return 200
    }

    /**
     * 响应消息，用于提供更详细的响应信息
     */
    override fun responseMessage(): String {
        return "success"
    }
}

// 默认失败响应信息
val defaultFailureResponseInformation = object : ResponseInformation {
    /**
     * 响应状态码，用于表示响应的状态
     */
    override fun responseStateCode(): Int {
        return 400
    }

    /**
     * 响应消息，用于提供更详细的响应信息
     */
    override fun responseMessage(): String {
        return "failure"
    }
}

/**
 * 统一响应封装类
 *
 * 该类用于封装所有接口的响应数据，继承自BaseResult，提供标准的响应结构。
 * 包含状态码、成功标志、消息、数据、请求ID和扩展信息。
 *
 * @since 2025-05-03 16:04:42
 */
data class R<T>(
    override val code: Int,
    override val success: Boolean,
    override val message: String,
    override val data: T? = null,
    override val requestId: String? = null,
    override val extra: Map<String, Any?> = emptyMap(),
) : BaseResult<T>(code, success, message, data, requestId, extra) {
    companion object {
        /**
         * 构建扩展信息映射表
         *
         * 该方法遍历扩展器列表，调用每个扩展器的extend方法来填充额外信息。
         * 主要用于在构建响应时添加可选的扩展字段。
         *
         * @param extenders 扩展信息提供者列表
         * @return 包含所有扩展信息的Map对象
         */
        private fun buildExtraMap(extenders: List<ResultExtender>): Map<String, Any?> {
            return mutableMapOf<String, Any?>().apply {
                extenders.forEach { it.extend(this) }
            }
        }

        /**
         * 创建成功响应对象
         *
         * 使用给定的响应信息对象创建一个成功的响应实例。默认使用预定义的成功信息。
         * 可指定数据内容、请求ID提供者以及一组扩展器以增强响应信息。
         *
         * @param info 响应信息对象，默认为defaultOkResponseInformation
         * @param data 响应数据，默认为null
         * @param requestIdProvider 请求ID提供者，默认为DefaultRequestIdProvider
         * @param extenders 扩展信息提供者列表，默认为空列表
         * @return 成功响应对象R<T>
         */
        fun <T> success(
            info: ResponseInformation = defaultOkResponseInformation,
            data: T? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(info.responseStateCode(), true, info.responseMessage(), data, reqId, extra)
        }

        /**
         * 创建失败响应对象
         *
         * 使用给定的响应信息对象创建一个失败的响应实例。默认使用预定义的失败信息。
         * 可指定数据内容、请求ID提供者以及一组扩展器以增强响应信息。
         *
         * @param info 响应信息对象，默认为defaultFailureResponseInformation
         * @param data 响应数据，默认为null
         * @param requestIdProvider 请求ID提供者，默认为DefaultRequestIdProvider
         * @param extenders 扩展信息提供者列表，默认为空列表
         * @return 失败响应对象R<T>
         */
        fun <T> failure(
            info: ResponseInformation = defaultFailureResponseInformation,
            data: T? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(info.responseStateCode(), false, info.responseMessage(), data, reqId, extra)
        }

        /**
         * 创建成功响应对象
         *
         * 使用指定的状态码和消息创建一个成功的响应实例。
         * 可指定数据内容、请求ID提供者以及一组扩展器以增强响应信息。
         *
         * @param code 响应码，默认为200
         * @param message 消息，默认为"success"
         * @param data 响应数据，默认为null
         * @param requestIdProvider 请求ID提供者，默认为DefaultRequestIdProvider
         * @param extenders 扩展信息提供者列表，默认为空列表
         * @return 成功响应对象R<T>
         */
        fun <T> success(
            code: Int = 200,
            message: String = "success",
            data: T? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, true, message, data, reqId, extra)
        }

        /**
         * 创建成功响应对象（参数顺序不同）
         *
         * 这个方法与另一个success方法功能相同，但参数顺序不同，
         * 提供了更灵活的数据传递方式，允许优先设置数据内容。
         *
         * @param data 响应数据，默认为null
         * @param code 响应码，默认为200
         * @param message 消息，默认为"success"
         * @param requestIdProvider 请求ID提供者，默认为DefaultRequestIdProvider
         * @param extenders 扩展信息提供者列表，默认为空列表
         * @return 成功响应对象R<T>
         */
        fun <T> success(
            data: T? = null,
            code: Int = 200,
            message: String = "success",
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, true, message, data, reqId, extra)
        }

        /**
         * 创建失败响应对象
         *
         * 使用指定的状态码和消息创建一个失败的响应实例。
         * 可指定数据内容、请求ID提供者以及一组扩展器以增强响应信息。
         *
         * @param code 响应码，默认为400
         * @param message 消息，默认为"failure"
         * @param data 响应数据，默认为null
         * @param requestIdProvider 请求ID提供者，默认为DefaultRequestIdProvider
         * @param extenders 扩展信息提供者列表，默认为空列表
         * @return 失败响应对象R<T>
         */
        fun <T> failure(
            code: Int = 400,
            message: String = "failure",
            data: T? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, false, message, data, reqId, extra)
        }
    }
}
