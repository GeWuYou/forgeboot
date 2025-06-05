package com.gewuyou.forgeboot.context.impl.filter

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.StringContextHolder
import com.gewuyou.forgeboot.context.impl.processor.ReactorProcessor
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * 上下文 Web 过滤器
 *
 * 用于在 Web 请求处理过程中维护和传递上下文信息，包括 ThreadLocal、MDC 和 Reactor Context。
 * 通过一系列的 [ContextProcessor] 实现对上下文的提取、注入和清理操作。
 *
 * @property contextProcessors 上下文处理器列表，用于处理上下文的提取与注入逻辑。
 * @property reactorProc 反应式上下文处理器，用于将上下文写入 Reactor 的 Context 中。
 *
 * @since 2025-06-04 15:54:43
 * @author gewuyou
 */
class ContextWebFilter(
    private val contextProcessors: List<ContextProcessor>,
    private val reactorProc: ReactorProcessor
) : WebFilter {
    /**
     * 执行过滤逻辑，在请求链中插入上下文管理操作。
     *
     * 此方法依次完成以下步骤：
     * 1. 提取当前上下文并构建新的上下文数据；
     * 2. 将上下文填充到 ThreadLocal 和 MDC；
     * 3. 将上下文注入到请求头中以构建新地请求；
     * 4. 继续执行请求链，并将上下文写入 Reactor Context；
     * 5. 最终清理 ThreadLocal、MDC 中的上下文。
     *
     * @param exchange 当前的 ServerWebExchange 对象，代表 HTTP 请求交换。
     * @param chain Web 过滤器链，用于继续执行后续的过滤器或目标处理逻辑。
     * @return 返回一个 Mono<Void> 表示异步完成的操作。
     */
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        // 从 StringContextHolder 快照获取当前上下文并转换为可变 Map
        val ctx = StringContextHolder.snapshot().toMutableMap()
        // 遍历所有 ContextProcessor，从请求中提取上下文信息到 ctx
        contextProcessors.forEach { it.extract(exchange, ctx) }
        // 将上下文写入 StringContextHolder（ThreadLocal）
        ctx.forEach(StringContextHolder::put)
        // 使用 MdcProcessor 将上下文注入到 MDC 中
        contextProcessors.forEach { it.inject(Unit, ctx) }
        // 构建新的 ServerWebExchange 实例
        val mutated = exchange.mutate()
        // 注入上下文到请求头中
        contextProcessors.forEach { it.inject(mutated, ctx) }
        // 继续执行过滤器链，同时将上下文写入 Reactor Context
        return chain.filter(mutated.build())
            .contextWrite(reactorProc.injectToReactor(ctx))
            .doFinally {
                // 清理 ThreadLocal + MDC 上下文
                contextProcessors.forEach { it.inject(Unit, mutableMapOf()) }
                StringContextHolder.clear()
            }
    }
}