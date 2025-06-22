package com.gewuyou.forgeboot.trace.impl.provider

import com.gewuyou.forgeboot.context.api.extension.get
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.api.config.TraceProperties


/**
 *跟踪请求ID提供商
 *
 * @since 2025-05-03 17:26:46
 * @author gewuyou
 */
class TraceRequestIdProvider(
    private val traceProperties: TraceProperties,
    private val contextHolder: ContextHolder
): RequestIdProvider {
    /**
     * 获取请求ID
     *
     * 返回一个唯一的字符串标识符作为请求ID
     *
     * @return 请求ID的字符串表示
     */
    override fun getRequestId(): String {
        return contextHolder[traceProperties.requestIdMdcKey] ?:throw RuntimeException("requestId is null")
    }
}