package com.gewuyou.forgeboot.trace.filter



import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.config.entities.TraceProperties
import com.gewuyou.forgeboot.trace.extension.isSkipRequest
import com.gewuyou.forgeboot.trace.util.RequestIdUtil
import org.slf4j.MDC
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * 反应式请求 ID 过滤器
 *
 * 该类的主要作用是在每个请求开始时生成或获取一个唯一的请求 ID，并将其设置到日志上下文中，
 * 以便在后续的日志记录中能够追踪到该请求。它还支持基于特定模式跳过某些请求的处理。
 *
 * @param traceProperties 配置属性，包含请求 ID 的头名称和 MDK 关键等信息
 * @since 2025-02-09 02:14:49
 * @author gewuyou
 */
class ReactiveRequestIdFilter(
    private val traceProperties: TraceProperties
)  : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        // 检测请求是否需要跳过
        if (request.isSkipRequest(traceProperties.ignorePatten)) {
            return chain.filter(exchange)
        }
        // 获取请求头中请求 ID 的名称和 MDK 中的键
        val requestIdHeader = traceProperties.requestIdHeaderName
        val requestIdMdcKey = traceProperties.requestIdMdcKey
        // 尝试从请求头中获取 requestId，如果存在则设置到 RequestIdUtil 中，否则生成一个新的 requestId
        request.headers[requestIdHeader]?.let {
            it.firstOrNull()?.let(RequestIdUtil::setRequestId) ?: RequestIdUtil.generateRequestId()
        } ?: RequestIdUtil.generateRequestId()
        // 获取当前的 requestId
        val currentRequestId = RequestIdUtil.getRequestId()
        // 将 requestId 设置到日志中
        MDC.put(requestIdMdcKey, currentRequestId)
        log.info("设置 Request id: $currentRequestId")
        // ✅ **创建新的 request 并更新 exchange**
        // 更新请求头，确保后续的请求处理中包含 requestId
        val mutatedRequest = request.mutate()
            .header(requestIdHeader, currentRequestId)
            .build()
        val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
        // 放行请求
        return chain.filter(mutatedExchange)
            // ✅ 让 Reactor 线程也能获取 requestId
            // 将 requestId 写入 Reactor 的上下文中，以便在异步处理中也能访问
            .contextWrite { ctx -> ctx.put(requestIdMdcKey, currentRequestId) }
            .doFinally {
                // 清理 MDC 中的 requestId，避免内存泄漏
                MDC.remove(requestIdMdcKey)
                // 将 requestId 清除
                RequestIdUtil.removeRequestId()
            }
    }
}
