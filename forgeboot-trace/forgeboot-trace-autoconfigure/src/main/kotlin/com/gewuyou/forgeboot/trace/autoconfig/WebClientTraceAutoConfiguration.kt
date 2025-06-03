package com.gewuyou.forgeboot.trace.autoconfig

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import com.gewuyou.forgeboot.trace.impl.filter.WebClientRequestIdFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Web客户端跟踪自动配置
 *
 * 该配置类用于自动配置Web客户端的请求ID过滤器，以实现请求跟踪功能
 * 它依赖于特定条件，如类的存在和Bean的定义，以确保在适当的时候进行配置
 *
 * @since 2025-05-31 21:59:02
 * @author gewuyou
 */
@Configuration
@ConditionalOnClass(name = ["org.springframework.web.reactive.function.client.WebClient\$Builder"])
open class WebClientTraceAutoConfiguration {

    /**
     * 配置Web客户端的请求ID过滤器
     *
     * 该方法在满足条件时被调用，它获取WebClient.Builder实例并应用请求ID过滤器，
     * 以便在发出请求时添加请求ID信息这对于跟踪请求跨多个服务非常有用
     *
     * @param webClientBuilder Web客户端构建器，用于配置过滤器
     * @param traceProperties 跟踪属性配置，用于定制跟踪行为
     * @return 配置后的WebClient.Builder实例
     */
    @Bean
    @ConditionalOnBean(name = ["webClientBuilder"])
    open fun webClientRequestIdFilter(
        webClientBuilder: org.springframework.web.reactive.function.client.WebClient.Builder,
        traceProperties: TraceProperties
    ): org.springframework.web.reactive.function.client.WebClient.Builder {
        log .info("配置Web客户端的请求ID过滤器")
        return webClientBuilder.filter(WebClientRequestIdFilter(traceProperties))
    }
}
