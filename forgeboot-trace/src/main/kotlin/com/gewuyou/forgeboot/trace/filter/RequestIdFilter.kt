package com.gewuyou.forgeboot.trace.filter


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.config.entities.TraceProperties
import com.gewuyou.forgeboot.trace.extension.isSkipRequest
import com.gewuyou.forgeboot.trace.util.RequestIdUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 请求过滤器
 *
 * 该过滤器用于在请求处理过程中添加和管理请求ID(RequestId)，以便于日志追踪和调试
 * 它基于Spring的OncePerRequestFilter，确保每个请求只被过滤一次
 *
 * @param traceProperties Trace属性配置，包含请求ID的相关配置信息
 * @since 2025-01-02 14:31:07
 * @author gewuyou
 */
class RequestIdFilter(
    private val traceProperties: TraceProperties
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        // 检查请求是否需要跳过
        if (request.isSkipRequest(traceProperties.ignorePatten)) {
            return chain.doFilter(request, response)
        }
        // 获取请求头中 requestId 的名称和 MDC 中的键
        val requestIdHeader = traceProperties.requestIdHeaderName
        val requestIdMdcKey = traceProperties.requestIdMdcKey
        try {
            // 尝试从请求头中获取 requestId
            request.getHeader(requestIdHeader)?.also(
                RequestIdUtil::setRequestId
            ) ?: run {
                // 如果没有，则生成新的 requestId
                RequestIdUtil.generateRequestId()
            }
            // 获取 requestId
            val requestId = RequestIdUtil.getRequestId()
            // 将requestId 设置到日志中
            MDC.put(requestIdMdcKey, requestId)
            log.info("设置 Request id: $requestId")
            // 将 requestId 设置到响应头中
            response.setHeader(requestIdHeader, requestId)
            // 继续处理请求
            chain.doFilter(request, response)
        } finally {
            // 移除 MDC 中的 requestId
            MDC.remove(requestIdMdcKey)
            // 清理当前线程的 RequestId，防止内存泄漏
            RequestIdUtil.removeRequestId()
        }
    }
}