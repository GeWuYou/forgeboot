package com.gewuyou.forgeboot.webmvc.exception.config

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.exception.config.entities.WebMvcExceptionProperties
import com.gewuyou.forgeboot.webmvc.exception.handler.GlobalExceptionHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

/**
 *Web MVC 异常自动配置
 *
 * @since 2025-05-13 11:48:01
 * @author gewuyou
 */
@Configuration
@EnableConfigurationProperties(WebMvcExceptionProperties::class)
class WebMvcExceptionAutoConfiguration {
    /**
     *默认消息解析器
     *
     * @since 2025-05-03 16:21:43
     * @author gewuyou
     */
    @Bean
    @Order(Int.MAX_VALUE)
    @ConditionalOnMissingBean
    fun defaultMessageResolver(): MessageResolver = MessageResolver { code, _ -> code }

    /**
     *默认请求ID提供商
     *
     * @since 2025-05-03 16:22:18
     * @author gewuyou
     */
    @Bean
    @Order(Int.MAX_VALUE)
    @ConditionalOnMissingBean
    fun defaultRequestIdProvider(): RequestIdProvider = RequestIdProvider { "" }

    @Bean
    @ConditionalOnMissingBean
    fun globalExceptionHandler(
        webMvcExceptionProperties: WebMvcExceptionProperties,
        requestIdProvider: RequestIdProvider,
    ): GlobalExceptionHandler {
        log.info("Web MVC 异常处理器 已创建！")
        return GlobalExceptionHandler(webMvcExceptionProperties, requestIdProvider)
    }

}