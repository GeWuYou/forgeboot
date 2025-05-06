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
 * @since 2025-05-03 16:04:42
 */
data class R<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val requestId: String? = null,
    val extra: Map<String, Any?> = emptyMap() // ✅ 扩展字段保存位置
) {
    /**
     * 转换为可变 Map，包含 extra 中的字段
     */
    fun toMutableFlatMap(): MutableMap<String, Any?> {
        val map = mutableMapOf(
            "code" to code,
            "success" to success,
            "message" to message,
            "data" to data
        )
        if (!requestId.isNullOrBlank()) {
            map["requestId"] = requestId
        }
        map.putAll(extra) // ✅ 扁平化合并
        return map
    }

    /**
     * 转换为不可变 Map，包含 extra 中的字段
     */
    fun toFlatMap(): Map<String, Any?> = toMutableFlatMap().toMap()

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
         * @param messageResolver 消息解析器
         * @param i18bArgs 国际化参数
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
         */
        fun <T> success(
            info: ResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            i18bArgs: Array<Any>? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(info.responseI8nMessageCode, i18bArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(info.responseCode, true, msg, data, reqId, extra)
        }

        /**
         * 创建失败响应对象
         *
         * @param info 响应信息对象
         * @param data 响应数据
         * @param messageResolver 消息解析器
         * @param i18bArgs 国际化参数
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 失败响应对象
         */
        fun <T> failure(
            info: ResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            i18bArgs: Array<Any>? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(info.responseI8nMessageCode, i18bArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(info.responseCode, false, msg, data, reqId, extra)
        }

        /**
         * 创建成功响应对象
         *
         * @param code 响应码
         * @param messageCode 消息码
         * @param data 响应数据
         * @param i18nArgs 国际化参数
         * @param messageResolver 消息解析器
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
         */
        fun <T> success(
            code: Int = 200,
            messageCode: String = "success",
            data: T? = null,
            i18nArgs: Array<Any>? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18nArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, true, msg, data, reqId, extra)
        }

        /**
         * 创建失败响应对象
         *
         * @param code 响应码
         * @param messageCode 消息码
         * @param data 响应数据
         * @param i18nArgs 国际化参数
         * @param messageResolver 消息解析器
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 失败响应对象
         */
        fun <T> failure(
            code: Int = 400,
            messageCode: String = "error",
            data: T? = null,
            i18nArgs: Array<Any>? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList()
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18nArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, false, msg, data, reqId, extra)
        }
    }
}