package com.gewuyou.forgeboot.webmvc.version.config

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.webmvc.version.config.entities.WebMvcVersionProperties
import com.gewuyou.forgeboot.webmvc.version.mapping.ApiVersionRequestMappingHandlerMapping
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

/**
 *版本自动配置
 *
 * @since 2025-04-26 13:09:58
 * @author gewuyou
 */
@Configuration
@EnableConfigurationProperties(WebMvcVersionProperties::class)
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
    open fun apiVersionRequestMappingHandlerMapping(
        webMvcVersionProperties: WebMvcVersionProperties,
        corsConfigurationSource: CorsConfigurationSource,
    ): RequestMappingHandlerMapping {
        log.info("创建 API 版本请求映射处理程序映射")
        return ApiVersionRequestMappingHandlerMapping(webMvcVersionProperties).also {
            it.order = Int.MIN_VALUE
            it.corsConfigurationSource = corsConfigurationSource
        }
    }
}