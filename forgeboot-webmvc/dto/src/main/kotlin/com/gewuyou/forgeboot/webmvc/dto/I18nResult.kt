package com.gewuyou.forgeboot.webmvc.dto


import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.i18n.api.I18nResponseInformation
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.dto.i18n.I18nKeys


/**
 *默认请求ID提供商
 *
 * @since 2025-05-03 16:22:18
 * @author gewuyou
 */
val DefaultRequestIdProvider : RequestIdProvider = RequestIdProvider{""}

/**
 *默认消息解析器
 *
 * @since 2025-05-03 16:21:43
 * @author gewuyou
 */
val DefaultMessageResolver : MessageResolver  = MessageResolver { code, _ -> code }

/**
 * 默认成功i18n响应信息对象
 *
 * 提供标准的成功响应定义，包含国际化支持
 * 响应码为200，表示操作成功
 * i18n消息码用于查找本地化文本
 *
 * @since 2025-05-03 16:23:00
 * @author gewuyou
 */
val defaultOkI18nResponseInformation: I18nResponseInformation = object : I18nResponseInformation {
    /**
     * 获取响应码
     * @return 响应码
     */
    override val responseCode: Int
        get() = 200

    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    override val responseI8nMessageCode: String
        get() = I18nKeys.Forgeboot.Webmvc.Dto.RESULT_RESPONSEINFO_OK

    /**
     * 获取i18n响应信息参数
     * @return 响应信息 参数数组
     */
    override val responseI8nMessageArgs: Array<Any>?
        get() = arrayOf()
}

/**
 * 默认失败i18n响应信息对象
 *
 * 提供标准的失败响应定义，包含国际化支持
 * 响应码为400，表示请求错误
 * i18n消息码用于查找本地化文本
 *
 * @since 2025-05-03 16:23:15
 * @author gewuyou
 */
val defaultFailureI18nResponseInformation: I18nResponseInformation = object : I18nResponseInformation {
    /**
     * 获取响应码
     * @return 响应码
     */
    override val responseCode: Int
        get() = 400

    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    override val responseI8nMessageCode: String
        get() = I18nKeys.Forgeboot.Webmvc.Dto.RESULT_RESPONSEINFO_FAIL

    /**
     * 获取i18n响应信息参数
     * @return 响应信息 参数数组
     */
    override val responseI8nMessageArgs: Array<Any>?
        get() = arrayOf()
}

/**
 * 统一响应封装类
 *
 * @since 2025-05-03 16:04:42
 */
data class I18nResult<T>(
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
         * @param messageResolver 消息解析器
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 成功响应对象
         */
        fun <T> success(
            info: I18nResponseInformation = defaultOkI18nResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): I18nResult<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(info.responseI8nMessageCode, info.responseI8nMessageArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return I18nResult(info.responseCode, true, msg, data, reqId, extra)
        }

        /**
         * 创建失败响应对象
         *
         * @param info 响应信息对象
         * @param data 响应数据
         * @param messageResolver 消息解析器
         * @param requestIdProvider 请求ID提供者
         * @param extenders 扩展信息提供者列表
         * @return 失败响应对象
         */
        fun <T> failure(
            info: I18nResponseInformation = defaultFailureI18nResponseInformation,
            data: T? = null,
            messageResolver: MessageResolver? = null,
            requestIdProvider: RequestIdProvider? = null,
            extenders: List<ResultExtender> = emptyList(),
        ): I18nResult<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(info.responseI8nMessageCode, info.responseI8nMessageArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return I18nResult(info.responseCode, false, msg, data, reqId, extra)
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
            extenders: List<ResultExtender> = emptyList(),
        ): I18nResult<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18nArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return I18nResult(code, true, msg, data, reqId, extra)
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
            extenders: List<ResultExtender> = emptyList(),
        ): I18nResult<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18nArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return I18nResult(code, false, msg, data, reqId, extra)
        }
    }
}