package com.gewuyou.forgeboot.trace.autoconfig


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.trace.impl.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.decorator.RequestIdTaskDecorator
import com.gewuyou.forgeboot.trace.impl.filter.ReactiveRequestIdFilter
import com.gewuyou.forgeboot.trace.impl.filter.RequestIdFilter
import com.gewuyou.forgeboot.trace.impl.provider.TraceRequestIdProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 跟踪自动配置
 *
 * 该配置类用于自动配置请求跟踪功能，包括Spring MVC、Spring WebFlux、Feign和异步线程池的请求ID生成和传递
 * @since 2025-03-17 16:33:40
 * @author gewuyou
 */
@Configuration
@EnableConfigurationProperties(TraceProperties::class)
open class TraceAutoConfiguration {
    /**
     * Spring MVC 过滤器（仅当 Spring MVC 存在时生效）
     *
     * 该过滤器用于在Spring MVC应用中生成和传递请求ID
     * @param traceProperties 跟踪配置属性
     * @return RequestIdFilter实例
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
    @ConditionalOnMissingBean
    open fun requestIdFilter(traceProperties: TraceProperties): RequestIdFilter {
        log.info("RequestIdFilter 已创建！")
        return RequestIdFilter(traceProperties)
    }

    /**
     * Spring WebFlux 过滤器（仅当 Spring WebFlux 存在时生效）
     *
     * 该过滤器用于在Spring WebFlux应用中生成和传递请求ID
     * @param traceProperties 跟踪配置属性
     * @return ReactiveRequestIdFilter实例
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
    @ConditionalOnMissingBean
    open fun reactiveRequestIdFilter(traceProperties: TraceProperties): ReactiveRequestIdFilter {
        log.info("ReactiveRequestIdFilter 已创建！")
        return ReactiveRequestIdFilter(traceProperties)
    }

    /**
     * 请求ID提供者（用于生成请求ID）
     *
     * 该提供者用于生成请求ID，默认为TraceRequestIdProvider
     * @return RequestIdProvider实例
     */
    @Bean
    @ConditionalOnMissingBean(RequestIdProvider::class)
    open fun traceRequestIdProvider(): TraceRequestIdProvider {
        log.info("TraceRequestIdProvider 已创建！")
        return TraceRequestIdProvider()
    }

    /**
     * 线程池装饰器（用于 @Async）
     *
     * 该装饰器用于在异步线程池中传递请求ID，确保异步执行的任务能够携带正确地请求信息
     * @param traceProperties 跟踪配置属性
     * @return RequestIdTaskDecorator实例
     */
    @Bean
    @ConditionalOnMissingBean
    open fun requestIdTaskDecorator(traceProperties: TraceProperties): RequestIdTaskDecorator {
        log.info("RequestIdTaskDecorator 已创建！")
        return RequestIdTaskDecorator(traceProperties)
    }

}
