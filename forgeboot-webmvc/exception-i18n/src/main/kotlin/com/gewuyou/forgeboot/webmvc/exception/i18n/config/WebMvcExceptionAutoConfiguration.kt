package com.gewuyou.forgeboot.webmvc.exception.i18n.config

import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.trace.api.RequestIdProvider
import com.gewuyou.forgeboot.webmvc.exception.i18n.config.entities.WebMvcExceptionI18nProperties
import com.gewuyou.forgeboot.webmvc.exception.i18n.handler.GlobalExceptionHandler
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
@EnableConfigurationProperties(WebMvcExceptionI18nProperties::class)
@Configuration
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
    fun i18nGlobalExceptionHandler(
        webMvcExceptionI18nProperties: WebMvcExceptionI18nProperties,
        messageResolver: MessageResolver,
        requestIdProvider: RequestIdProvider,
    ): GlobalExceptionHandler {
        log.info("本地化全局异常处理程序创建成功!")
        return GlobalExceptionHandler(
            webMvcExceptionI18nProperties,
            messageResolver,
            requestIdProvider
        )
    }
}