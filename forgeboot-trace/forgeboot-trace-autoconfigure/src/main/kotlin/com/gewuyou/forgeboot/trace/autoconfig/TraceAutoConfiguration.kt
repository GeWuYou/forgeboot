package com.gewuyou.forgeboot.trace.autoconfig


import com.gewuyou.forgeboot.context.api.ContextFieldContributor
import com.gewuyou.forgeboot.context.api.entities.FieldDef
import com.gewuyou.forgeboot.context.api.enums.Scope
import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.provider.TraceRequestIdProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

/**
 * 跟踪自动配置
 *
 * 该配置类用于自动配置请求跟踪功能，包括Spring MVC、Spring WebFlux、Feign和异步线程池的请求ID生成和传递
 * @since 2025-03-17 16:33:40
 * @author gewuyou
 */
@Configuration
@EnableConfigurationProperties(TraceProperties::class)
class TraceAutoConfiguration(
    private val traceProperties: TraceProperties
) {
    /**
     * 请求ID提供者（用于生成请求ID）
     *
     * 该提供者用于生成请求ID，默认为TraceRequestIdProvider
     * @return RequestIdProvider实例
     */
    @Bean
    @ConditionalOnMissingBean(RequestIdProvider::class)
    fun traceRequestIdProvider(): TraceRequestIdProvider {
        log.info("TraceRequestIdProvider 已创建！")
        return TraceRequestIdProvider(traceProperties)
    }
    @Bean
    fun requestContributor() = ContextFieldContributor {
        setOf(
            FieldDef(
                header = traceProperties.requestIdHeaderName,          // 请求-响应头名
                key = traceProperties.requestIdMdcKey,             // ctx/MDC 键
                generator = { UUID.randomUUID().toString() }, // 如果前端没带，用这个生成
                scopes = setOf(Scope.HEADER, Scope.MDC, Scope.REACTOR)
            )
        )
    }
}
