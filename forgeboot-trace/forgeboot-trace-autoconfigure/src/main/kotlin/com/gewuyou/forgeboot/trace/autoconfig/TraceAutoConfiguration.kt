package com.gewuyou.forgeboot.trace.autoconfig


import com.gewuyou.forgeboot.context.api.ContextFieldContributor
import com.gewuyou.forgeboot.context.api.entities.FieldDef
import com.gewuyou.forgeboot.context.api.enums.Scope
import com.gewuyou.forgeboot.context.impl.ContextHolder
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.provider.TraceRequestIdProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.util.*

/**
 * 跟踪自动配置
 *
 * 该配置类用于自动配置请求跟踪功能，包括Spring MVC、Spring WebFlux、Feign和异步线程池的请求ID生成和传递
 * @since 2025-03-17 16:33:40
 * @author gewuyou
 */
@AutoConfiguration
@EnableConfigurationProperties(TraceProperties::class)
class TraceAutoConfiguration(
    private val traceProperties: TraceProperties,
) {
    /**
     * 创建请求ID提供者Bean
     *
     * 用于生成分布式请求链路追踪所需的唯一请求标识
     * @param contextHolder 上下文持有者，用于跨组件传递请求上下文
     * @return 初始化完成的TraceRequestIdProvider实例
     */
    @Bean
    @ConditionalOnMissingBean(RequestIdProvider::class)
    fun traceRequestIdProvider(contextHolder: ContextHolder): TraceRequestIdProvider {
        log.info("TraceRequestIdProvider 已创建！")
        return TraceRequestIdProvider(traceProperties,contextHolder)
    }

    /**
     * 创建请求上下文贡献者Bean
     *
     * 定义请求上下文中需要维护的字段定义集合
     * @return ContextFieldContributor实例，包含完整的上下文字段定义
     */
    @Bean
    fun requestContributor() = ContextFieldContributor {
        setOf(
            FieldDef(
                // 请求-响应头名
                header = traceProperties.requestIdHeaderName,
                // ctx/MDC 键
                key = traceProperties.requestIdMdcKey,
                // 前端未携带时的生成策略
                generator = { UUID.randomUUID().toString() },
                // 作用域范围
                scopes = setOf(Scope.HEADER, Scope.MDC, Scope.REACTOR)
            )
        )
    }
}
