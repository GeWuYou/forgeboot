package com.gewuyou.forgeboot.context.autoconfigure


import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.context.api.ContextFieldContributor
import com.gewuyou.forgeboot.context.api.ContextProcessor
import com.gewuyou.forgeboot.context.api.FieldRegistry
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.context.impl.DefaultFieldRegistry
import com.gewuyou.forgeboot.context.impl.filter.ContextServletFilter
import com.gewuyou.forgeboot.context.impl.filter.ContextWebFilter
import com.gewuyou.forgeboot.context.impl.processor.GeneratorProcessor
import com.gewuyou.forgeboot.context.impl.processor.HeaderProcessor
import com.gewuyou.forgeboot.context.impl.processor.MdcProcessor
import com.gewuyou.forgeboot.context.impl.processor.ReactorProcessor
import com.gewuyou.forgeboot.core.serialization.serializer.ValueSerializer
import com.gewuyou.forgeboot.core.serialization.serializer.impl.serializer.JacksonValueSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.web.reactive.function.client.ClientRequest

/**
 * 配置类，用于自动配置上下文相关的 Bean。
 *
 * 该配置类根据不同的运行时依赖和配置条件，定义了一系列的 Bean，
 * 实现了上下文字段在不同场景下的传播与管理机制。
 *
 * @since 2025-06-04 11:48:01
 * @author gewuyou
 */
@Configuration
class ForgeContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun valueSerializer(objectMapper: ObjectMapper): ValueSerializer{
        return JacksonValueSerializer(objectMapper)
    }
    @Bean
    @ConditionalOnMissingBean
    fun contextHolder(valueSerializer: ValueSerializer): ContextHolder {
        return ContextHolder(valueSerializer)
    }
    /* ───────────────────────────────────────────────────────────────
       0️⃣ 通用 Bean：不依赖 Web / Feign / Reactor 等外部包
    ─────────────────────────────────────────────────────────────── */

    /**
     * 创建 FieldRegistry Bean，用于注册上下文中所有字段定义。
     *
     * FieldRegistry 是上下文字段的核心注册中心，聚合所有 ContextFieldContributor 提供的字段定义。
     *
     * @param contributors 提供字段定义的贡献者列表
     * @return 构建完成的 FieldRegistry 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun fieldRegistry(contributors: List<ContextFieldContributor>): FieldRegistry =
        DefaultFieldRegistry(contributors.flatMap { it.fields() }.toSet())

    /**
     * 创建 HeaderProcessor Bean，用于处理请求头中的上下文字段。
     *
     * HeaderProcessor 负责从请求头中提取上下文字段并注入到当前线程上下文中。
     *
     * @param reg 字段注册表
     * @return 构建完成的 HeaderProcessor 实例
     */
    @Bean("headerProcessor")
    fun headerProcessor(reg: FieldRegistry) = HeaderProcessor(reg)

    /**
     * 创建 GeneratorProcessor Bean，用于生成上下文字段值。
     *
     * GeneratorProcessor 根据字段定义生成默认值（如 traceId、spanId 等），适用于首次进入系统的情况。
     *
     * @param reg 字段注册表
     * @return 构建完成的 GeneratorProcessor 实例
     */
    @Bean("generatorProcessor")
    fun generatorProcessor(reg: FieldRegistry) = GeneratorProcessor(reg)

    /**
     * 创建 MdcProcessor Bean，用于将上下文字段写入 MDC（Mapped Diagnostic Context）。
     *
     * MdcProcessor 使得日志框架（如 Logback）可以访问当前上下文字段，便于日志追踪。
     *
     * @param reg 字段注册表
     * @return 构建完成的 MdcProcessor 实例
     */
    @Bean("mdcProcessor")
    fun mdcProcessor(reg: FieldRegistry) = MdcProcessor(reg)

    /* ───────────────────────────────────────────────────────────────
       1️⃣ Reactor 支持（只有 classpath 有 Reactor 时才激活）
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，提供对 Reactor 上下文传播的支持。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = ["reactor.util.context.Context"])
    class ReactorSupport {

        /**
         * 创建 ReactorProcessor Bean，用于在 Reactor 上下文中传播上下文字段。
         *
         * ReactorProcessor 适配了 Reactor 的 Context 接口，确保上下文字段在响应式流中正确传递。
         *
         * @param reg 字段注册表
         * @return 构建完成的 ReactorProcessor 实例
         */
        @Bean("reactorProcessor")
        fun reactorProcessor(reg: FieldRegistry) = ReactorProcessor(reg)
    }

    /* ───────────────────────────────────────────────────────────────
       2️⃣ WebFlux 过滤器（依赖 WebFlux + Reactor）
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，注册 WebFlux 环境下的上下文传播过滤器。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
    @ConditionalOnClass(name = ["org.springframework.web.server.WebFilter"])
    class WebFluxPart {

        /**
         * 注册 ContextWebFilter Bean，用于在 WebFlux 请求链中传播上下文字段。
         *
         * 该过滤器利用 ReactorProcessor 在 WebFlux 的过滤器链中维护上下文一致性。
         *
         * @param chain 处理器链
         * @param reactorProcessor ReactorProcessor 实例
         * @return 构建完成的 ContextWebFilter 实例
         */
        @Bean
        @ConditionalOnMissingBean
        fun contextWebFilter(
            chain: List<ContextProcessor>,
            reactorProcessor: ReactorProcessor,
            contextHolder: ContextHolder
        ) = ContextWebFilter(chain, reactorProcessor,contextHolder)
    }

    /* ───────────────────────────────────────────────────────────────
       3️⃣ Servlet 过滤器（依赖 Servlet API）
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，注册 Servlet 环境下的上下文传播过滤器。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
    @ConditionalOnClass(name = ["jakarta.servlet.Filter"])
    class ServletPart {

        /**
         * 注册 ContextServletFilter Bean，用于在 Servlet 请求链中传播上下文字段。
         *
         * 该过滤器负责在同步阻塞的 Servlet 请求链中维护上下文一致性。
         *
         * @param chain 处理器链
         * @return 构建完成的 ContextServletFilter 实例
         */
        @Bean
        @ConditionalOnMissingBean
        fun contextServletFilter(chain: List<ContextProcessor>,contextHolder: ContextHolder) =
            ContextServletFilter(chain,contextHolder)
    }

    /* ───────────────────────────────────────────────────────────────
       4️⃣ Feign 请求拦截器（依赖 OpenFeign）
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，注册 Feign 客户端的请求拦截器。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = ["feign.RequestInterceptor"])
    class FeignPart {

        /**
         * 注册 Feign 请求拦截器，用于在 Feign 调用中传播上下文字段。
         *
         * 拦截器会在每次 Feign 请求发起前，将当前上下文字段写入 HTTP 请求头。
         *
         * @param registry 字段注册表
         * @param contextHolder 上下文持有者
         * @return 构建完成的 feign.RequestInterceptor 实例
         */
        @Bean
        @ConditionalOnMissingBean
        fun feignInterceptor(registry: FieldRegistry,contextHolder: ContextHolder) =
            feign.RequestInterceptor { tpl ->
                val ctx = contextHolder.snapshot()
                registry.all().forEach { def ->
                    ctx[def.key]?.let { tpl.header(def.header, it) }
                }
            }
    }

    /* ───────────────────────────────────────────────────────────────
       5️⃣ 线程池 TaskDecorator（纯 Spring，安全通用）
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，注册异步执行上下文保持支持。
     */
    @Configuration(proxyBeanMethods = false)
    class TaskDecoratorPart {

        /**
         * 创建 TaskDecorator Bean，用于在异步执行中保持上下文一致性。
         *
         * 通过装饰线程池任务，确保异步任务继承调用线程的上下文状态。
         *
         * @param processors 所有处理器列表
         * @param contextHolder 上下文持有者
         * @return 构建完成的 TaskDecorator 实例
         */
        @Bean
        fun contextTaskDecorator(processors: List<ContextProcessor>,contextHolder: ContextHolder) =
            TaskDecorator { delegate ->
                val snap = contextHolder.snapshot()
                Runnable {
                    try {
                        snap.forEach(contextHolder::put)
                        processors.forEach { it.inject(Unit, snap.toMutableMap()) }
                        delegate.run()
                    } finally {
                        processors.forEach { it.inject(Unit, mutableMapOf()) }
                        contextHolder.clear()
                    }
                }
            }
    }

    /* ───────────────────────────────────────────────────────────────
       6️⃣ WebClient 过滤器
    ─────────────────────────────────────────────────────────────── */

    /**
     * 配置类，注册 WebClient 自定义器以支持上下文传播。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = ["org.springframework.web.reactive.function.client.WebClient"])
    class WebClientPart(private val registry: FieldRegistry) {

        /**
         * 注册 WebClientCustomizer，用于定制 WebClient 的请求行为。
         *
         * 在每次请求发出前，将当前上下文字段写入 HTTP 请求头。
         *@param contextHolder 上下文持有者
         * @return 构建完成的 WebClientCustomizer 实例
         */
        @Bean
        fun contextWebClientCustomizer(contextHolder: ContextHolder) = WebClientCustomizer { builder ->
            builder.filter { req, next ->
                val ctx = contextHolder.snapshot()
                val mutated = ClientRequest.from(req).apply {
                    registry.all().forEach { def ->
                        ctx[def.key]?.let { value -> header(def.header, value) }
                    }
                }.build()
                next.exchange(mutated)
            }
        }
    }
}