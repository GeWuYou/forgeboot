/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.i18n.autoconfigure


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.i18n.api.InfoResolver
import com.gewuyou.forgeboot.i18n.api.config.I18nProperties
import com.gewuyou.forgeboot.i18n.impl.filter.ReactiveLocaleResolver
import com.gewuyou.forgeboot.i18n.impl.resolver.MessageSourceInfoResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.util.StringUtils
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*

/**
 * 本地化配置
 *
 * @author gewuyou
 * @since 2024-11-11 00:46:01
 */
@AutoConfiguration
@EnableConfigurationProperties(I18nProperties::class)
class I18nAutoConfiguration {
    /**
     * 创建并配置一个基于MessageSource的信息解析器
     *
     * 该方法通过Spring的条件注解有选择性地创建一个InfoResolver实例，
     * 当容器中不存在InfoResolver类型的Bean时，才会创建此Bean。
     * 主要用于解决国际化消息的解析问题，将InfoLike对象解析为具体的本地化消息。
     *
     * @param forgebootI18nMessageSource 一个MessageSource实例，用于解析国际化消息
     * @return 返回一个InfoResolver实例，用于在国际化的环境中解析消息
     */
    @Bean
    @ConditionalOnMissingBean(InfoResolver::class)
    fun messageSourceInfoResolver(forgebootI18nMessageSource: MessageSource): InfoResolver {
        return MessageSourceInfoResolver(forgebootI18nMessageSource)
    }

    /**
     * 配置并创建一个区域设置解析器
     *
     * 此方法创建一个自定义的区域设置解析器，它首先尝试从请求参数中解析语言设置，
     * 如果参数不存在，则使用请求头中的语言设置
     *
     * @param i18nProperties 本地化属性配置
     * @return LocaleResolver 区域设置解析器
     */
    @Bean("forgebootI18nLocaleResolver")
    @ConditionalOnClass(name = ["org.springframework.web.servlet.DispatcherServlet"])
    fun localeResolver(i18nProperties: I18nProperties): LocaleResolver {
        return object : AcceptHeaderLocaleResolver() {
            override fun resolveLocale(request: HttpServletRequest): Locale {
                log.debug("开始解析URL参数 lang 语言配置...")
                // 先检查URL参数 ?lang=xx
                val lang = request.getParameter(i18nProperties.langRequestParameter)
                if (StringUtils.hasText(lang)) {
                    return Locale.forLanguageTag(lang)
                }
                // 设置默认语言为简体中文
                this.defaultLocale = Locale.forLanguageTag(i18nProperties.defaultLocale)
                // 返回请求头 Accept-Language 的语言配置
                return super.resolveLocale(request)
            }
        }
    }

    /**
     * 创建一个适用于 WebFlux 的区域设置解析器
     *
     * 当项目配置为 Reactive 类型时，此方法会被调用，用于创建一个 Reactive 类型的区域设置解析器
     *
     * @param i18nProperties 本地化属性配置
     * @return ReactiveLocaleResolver 适用于 WebFlux 的区域设置解析器
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
    fun createReactiveLocaleResolver(i18nProperties: I18nProperties): ReactiveLocaleResolver {
        log.info("创建 WebFlux 区域设置解析器...")
        return ReactiveLocaleResolver(i18nProperties)
    }
}
