package com.gewuyou.forgeboot.context.impl.filter

import com.gewuyou.forgeboot.context.impl.element.MdcContextElement
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.stream.Collectors

/**
 * 协程 MDC Web 过滤器
 *
 * 用于在响应式编程环境下，将上下文（如 MDC）从 Reactor Context 传递到 Kotlin 协程中，
 * 确保日志上下文信息能够正确传播。
 *
 * @since 2025-07-16 11:07:44
 * @author gewuyou
 */
class CoroutineMdcWebFilter : WebFilter {

    /**
     * 执行过滤操作的方法
     *
     * @param exchange 表示当前的服务器 Web 交换信息，包含请求和响应
     * @param chain 当前的过滤器链，用于继续执行后续的过滤器或目标处理器
     * @return 返回一个 Mono<Void>，表示异步完成的过滤操作
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return Mono.deferContextual { ctxView ->
            // 从 Reactor 上下文中提取键值对，筛选出 key 和 value 均为 String 类型的条目
            val mdcMap = ctxView.stream()
                .filter { it.key is String && it.value is String }
                .collect(Collectors.toMap(
                    { it.key as String },
                    { it.value as String }
                ))

            // 在带有 MDC 上下文的协程中执行过滤链
            mono(MdcContextElement(mdcMap)) {
                chain.filter(exchange).awaitFirstOrNull()
            }
        }
    }
}