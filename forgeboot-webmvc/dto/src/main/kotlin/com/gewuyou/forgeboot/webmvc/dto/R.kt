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
        private fun buildExtraMap(extenders: List<ResultExtender>): Map<String, Any?> {
            return mutableMapOf<String, Any?>().apply {
                extenders.forEach { it.extend(this) }
            }
        }

        /**
         * 创建成功响应对象
         *
         * @param info 响应信息对象
         * @param data 响应数据
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
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
         * @param info 响应信息对象
         * @param data 响应数据
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 失败响应对象
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
         * @param code 响应码
         * @param message 消息
         * @param data 响应数据
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
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
         * 创建成功响应对象
         *
         * @param code 响应码
         * @param message 消息
         * @param data 响应数据
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
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
         * @param code 响应码
         * @param message 消息
         * @param data 响应数据
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 失败响应对象
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
