package com.gewuyou.forgeboot.context.impl.filter

import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.element.CoroutineContextMapElement
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
 * @property contextHolder 上下文持有者实例，用于存储和管理 MDC 上下文数据
 *
 * @since 2025-07-16 11:07:44
 * @author gewuyou
 */
class CoroutineMdcWebFilter(
    private val contextHolder: ContextHolder
) : WebFilter {

    /**
     * 执行过滤操作的方法
     *
     * 在响应式流执行过程中拦截并提取 Reactor Context 中的 MDC 数据，
     * 将其封装为协程上下文并在新的协程环境中继续执行过滤链，
     * 以确保日志上下文信息在异步非阻塞处理流程中正确传播。
     *
     * @param exchange 表示当前的服务器 Web 交换信息，包含请求和响应
     * @param chain 当前的过滤器链，用于继续执行后续的过滤器或目标处理器
     * @return 返回一个 Mono<Void>，表示异步完成的过滤操作
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return Mono.deferContextual { ctxView ->
            /**
             * 从 Reactor 上下文中提取键值对，筛选出 key 和 value 均为 String 类型的条目
             * 构建 MDC 数据映射表，用于传递日志上下文信息
             */
            val mdcMap = ctxView.stream()
                .filter { it.key is String && it.value is String }
                .collect(Collectors.toMap(
                    { it.key as String },
                    { it.value as String }
                ))

            /**
             * 创建带有 MDC 上下文的协程环境并执行过滤链
             * 1. 将 MDC 数据注入协程上下文
             * 2. 将上下文数据同步到 ContextHolder
             * 3. 执行后续过滤器链并等待结果
             */
            mono(CoroutineContextMapElement(contextHolder,mdcMap)) {
                contextHolder.putAll(mdcMap)
                chain.filter(exchange).awaitFirstOrNull()
            }
        }
    }
}