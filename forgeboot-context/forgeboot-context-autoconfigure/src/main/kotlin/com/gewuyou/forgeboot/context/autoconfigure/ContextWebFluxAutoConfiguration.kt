package com.gewuyou.forgeboot.context.autoconfigure

import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.filter.ContextWebFilter
import com.gewuyou.forgeboot.context.impl.filter.CoroutineMdcWebFilter
import com.gewuyou.forgeboot.context.impl.processor.ReactorProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 * Context Web Flux 自动配置
 *
 * 该配置类用于在 Spring WebFlux 环境下自动装配上下文传播相关的组件。
 * 主要功能是注册 ContextWebFilter Bean，以确保请求链中上下文字段的一致性维护。
 *
 * @since 2025-06-24 22:16:19
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
@ConditionalOnClass(name = ["org.springframework.web.server.WebFilter"])
class ContextWebFluxAutoConfiguration {
    /**
     * 注册 ContextWebFilter Bean，用于在 WebFlux 请求链中传播上下文字段。
     *
     * 该过滤器利用 ReactorProcessor 在 WebFlux 的过滤器链中维护上下文一致性。
     * 参数 chain 提供了一组 ContextProcessor 实例，用于处理不同类型的上下文逻辑；
     * reactorProcessor 是一个关键组件，负责实际的上下文传播和反应式流集成；
     * contextHolder 存储了当前线程或请求的上下文数据，确保其在整个请求生命周期中可用。
     *
     * @param chain 处理器链，包含多个 ContextProcessor 实例
     * @param reactorProcessor ReactorProcessor 实例，用于反应式上下文传播
     * @param contextHolder 上下文持有者，用于存储和获取当前上下文
     * @return 构建完成的 ContextWebFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    fun contextWebFilter(
        chain: List<ContextProcessor>,
        reactorProcessor: ReactorProcessor,
        contextHolder: ContextHolder
    ) = ContextWebFilter(chain, reactorProcessor, contextHolder)
    /**
     * 注册 CoroutineMdcWebFilter Bean，用于在协程环境中传播 MDC 上下文信息。
     *
     * MDC（Mapped Diagnostic Context）通常用于存储线程上下文数据，在异步或协程编程模型中，
     * 需要特殊的处理以确保上下文能够在不同协程之间正确传递。
     * 该过滤器通过注册为 Spring WebFlux 的 WebFilter，确保请求链路中的 MDC 数据一致性。
     *
     * @return 构建完成的 CoroutineMdcWebFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE + 11) // 稍晚于 ContextWebFilter 执行
    fun coroutineMdcWebFilter(contextHolder: ContextHolder): CoroutineMdcWebFilter = CoroutineMdcWebFilter(contextHolder)
}