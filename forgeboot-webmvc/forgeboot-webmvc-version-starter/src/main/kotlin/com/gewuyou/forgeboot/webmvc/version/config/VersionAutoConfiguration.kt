package com.gewuyou.forgeboot.webmvc.version.config

import com.gewuyou.forgeboot.webmvc.version.mapping.ApiVersionRequestMappingHandlerMapping
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *版本自动配置
 *
 * @since 2025-04-26 13:09:58
 * @author gewuyou
 */
@Configuration
open class VersionAutoConfiguration {
    /**
     * 创建并配置一个 ApiVersionRequestMappingHandlerMapping 实例
     *
     * 该方法通过注解 @Bean 标记，表明此方法会返回一个由 Spring 管理的 Bean 实例
     * ApiVersionRequestMappingHandlerMapping 是一个自定义的 HandlerMapping，
     * 它支持基于 API 版本的请求映射处理，这对于需要版本控制的 RESTful API 设计非常有用
     *
     * @return ApiVersionRequestMappingHandlerMapping 实例，用于处理基于 API 版本的请求映射
     */
    @Bean
    open fun apiVersionRequestMappingHandlerMapping(): ApiVersionRequestMappingHandlerMapping {
        return ApiVersionRequestMappingHandlerMapping()
    }
}