package com.gewuyou.forgeboot.trace.impl.interceptor



import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.util.RequestIdUtil
import feign.RequestInterceptor
import feign.RequestTemplate

/**
 * Feign请求ID 拦截器
 *
 * @since 2025-03-17 16:42:50
 * @author gewuyou
 */
class FeignRequestIdInterceptor(
    private val traceProperties: TraceProperties
) : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        // 尝试获取当前请求的请求id
        val requestId = RequestIdUtil.requestId
        requestId?.let {
            // 如果请求id存在，则添加到请求头中
            template.header(traceProperties.requestIdHeaderName, requestId)
        } ?: run {
            log.warn("请求ID为null，请检查您是否已在过滤链中添加了请求filter。")
        }
    }
}