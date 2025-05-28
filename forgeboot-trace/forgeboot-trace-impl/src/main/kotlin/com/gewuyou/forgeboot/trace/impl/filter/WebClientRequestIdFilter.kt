package com.gewuyou.forgeboot.trace.impl.filter

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.impl.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.extension.isSkipRequest
import com.gewuyou.forgeboot.trace.impl.util.RequestIdUtil

import org.slf4j.MDC
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

/**
 * Web客户端请求ID过滤器
 *
 * 该类的主要作用是在发出HTTP请求时，确保请求ID的正确传递和记录
 * 如果请求被忽略，则直接传递不做处理；否则，会尝试从请求头中获取请求ID，
 * 如果获取不到则生成新的请求ID，并将其设置到请求头中以及日志中，以便于追踪请求
 *
 * @param traceProperties 追踪属性配置，包括忽略模式、请求ID头名称和MDK中的键
 * @since 2025-05-02 22:18:06
 * @author gewuyou
 */
class WebClientRequestIdFilter(
    private val traceProperties: TraceProperties
) : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        // 检查请求是否被忽略，如果被忽略，则直接执行请求
        if (request.isSkipRequest(traceProperties.ignorePatten)) {
            return next.exchange(request)
        }
        // 获取请求头中请求 ID 的名称和 MDK 中的键
        val requestIdHeader = traceProperties.requestIdHeaderName
        val requestIdMdcKey = traceProperties.requestIdMdcKey
        // 尝试从请求头中获取 requestId，如果存在则设置到 RequestIdUtil 中，否则生成一个新的 requestId
        request.headers()[requestIdHeader]?.let {
            it.firstOrNull()?.let(RequestIdUtil::requestId::set) ?: RequestIdUtil.generateRequestId()
        } ?: RequestIdUtil.generateRequestId()
        // 获取当前的 requestId
        val currentRequestId = RequestIdUtil.requestId
        // 将 requestId 设置到日志中
        MDC.put(requestIdMdcKey, currentRequestId)
        log.info("设置 Request id: $currentRequestId")
        // 创建一个新的请求，包含 requestId 头
        val mutatedRequest = ClientRequest.from(request)
            .header(requestIdHeader, currentRequestId)
            .build()
        // 执行请求，并在请求完成后清除 MDC 和 RequestIdUtil 中的 requestId
        return next.exchange(mutatedRequest)
            .doFinally {
                MDC.remove(requestIdMdcKey)
                RequestIdUtil.removeRequestId()
            }
    }

}
