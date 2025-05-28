package com.gewuyou.forgeboot.i18n.impl.filter


import com.gewuyou.forgeboot.i18n.api.WebFluxLocaleResolver
import com.gewuyou.forgeboot.i18n.impl.config.I18nProperties

import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*


/**
 *反应式 locale 解析器
 *
 * @since 2025-02-19 00:06:45
 * @author gewuyou
 */
class ReactiveLocaleResolver(
    private val i18nProperties: I18nProperties
): WebFluxLocaleResolver {
    private val log = LoggerFactory.getLogger(ReactiveLocaleResolver::class.java)
    /**
     * Process the Web request and (optionally) delegate to the next
     * `WebFilter` through the given [WebFilterChain].
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return `Mono<Void>` to indicate when request processing is complete
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val lang = exchange.request.queryParams.getFirst(i18nProperties.langRequestParameter)
        if (StringUtils.hasText(lang)) {
            log.info("解析到 lang 参数：$lang")
            LocaleContextHolder.setLocale(Locale.forLanguageTag(lang)) // 设置语言环境
        } else {
            // 如果没有 lang 参数，使用 Accept-Language 请求头
            val acceptLanguage = exchange.request.headers.getFirst("Accept-Language")
            log.info("解析到 Accept-Language 请求头：$acceptLanguage")
            if (StringUtils.hasText(acceptLanguage)) {
                // 设置语言环境
                LocaleContextHolder.setLocale(Locale.forLanguageTag(acceptLanguage))
            }else {
                // 如果没有 Accept-Language 请求头，使用默认语言环境
                LocaleContextHolder.setLocale(Locale.forLanguageTag(i18nProperties.defaultLocale))
            }
        }
        // 继续处理请求
        return chain.filter(exchange)
    }
}