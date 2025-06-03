package com.gewuyou.forgeboot.trace.autoconfig

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.trace.api.config.TraceProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *Feign跟踪自动配置
 *
 * @since 2025-05-31 22:02:56
 * @author gewuyou
 */
@Configuration
@ConditionalOnClass(name = ["feign.RequestInterceptor"])
open class FeignTraceAutoConfiguration {
    /**
     * Feign 拦截器（仅当 Feign 存在时生效）
     *
     * 该拦截器用于在Feign客户端中传递请求ID
     * @param traceProperties 跟踪配置属性
     * @return FeignRequestIdInterceptor实例
     */
    @Bean
    @ConditionalOnMissingBean(name = ["feignRequestIdInterceptor"])
    open fun feignRequestIdInterceptor(traceProperties: TraceProperties): Any {
        val clazz = Class.forName("com.gewuyou.forgeboot.trace.impl.interceptor.FeignRequestIdInterceptor")
        val constructor = clazz.getConstructor(TraceProperties::class.java)
        log.info( "创建FeignRequestIdInterceptor实例")
        return constructor.newInstance(traceProperties)
    }
}