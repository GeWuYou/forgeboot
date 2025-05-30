package com.gewuyou.forgeboot.trace.impl.provider

import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.impl.util.RequestIdUtil


/**
 *跟踪请求ID提供商
 *
 * @since 2025-05-03 17:26:46
 * @author gewuyou
 */
class TraceRequestIdProvider: RequestIdProvider {
    /**
     * 获取请求ID
     *
     * 返回一个唯一的字符串标识符作为请求ID
     *
     * @return 请求ID的字符串表示
     */
    override fun getRequestId(): String {
        return RequestIdUtil.requestId?:throw RuntimeException("requestId is null")
    }
}