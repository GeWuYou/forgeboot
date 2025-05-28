package com.gewuyou.forgeboot.webmvc.dto


import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.i18n.api.ResponseInformation
import com.gewuyou.forgeboot.trace.api.RequestIdProvider


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
 * 结果扩展器
 *
 * 用于扩展结果映射，通过实现此接口，可以自定义逻辑以向结果映射中添加、修改或删除元素
 * 主要用于在某个处理流程结束后，对结果数据进行额外的处理或装饰
 *
 * @since 2025-05-03 16:08:55
 * @author gewuyou
 */
fun interface ResultExtender {
    /**
     * 扩展结果映射
     *
     * 实现此方法以执行扩展逻辑，可以访问并修改传入的结果映射
     * 例如，可以用于添加额外的信息，修改现有值，或者根据某些条件删除条目
     *
     * @param resultMap 一个包含结果数据的可变映射，可以在此方法中对其进行修改
     */
    fun extend(resultMap: MutableMap<String, Any?>)
}

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
    val extra: Map<String, Any?> = emptyMap(),
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
        map.putAll(extra)
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
            extenders: List<ResultExtender> = emptyList(),
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
            extenders: List<ResultExtender> = emptyList(),
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
            extenders: List<ResultExtender> = emptyList(),
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
            extenders: List<ResultExtender> = emptyList(),
        ): R<T> {
            val msg = (messageResolver ?: DefaultMessageResolver).resolve(messageCode, i18nArgs)
            val reqId = (requestIdProvider ?: DefaultRequestIdProvider).getRequestId()
            val extra = buildExtraMap(extenders)
            return R(code, false, msg, data, reqId, extra)
        }
    }
}