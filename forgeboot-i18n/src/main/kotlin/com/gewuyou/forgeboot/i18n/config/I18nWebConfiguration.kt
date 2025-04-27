package com.gewuyou.forgeboot.i18n.config

import com.gewuyou.forgeboot.core.extension.log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor

/**
 *i18n web 配置
 *
 * @since 2025-02-18 23:33:39
 * @author gewuyou
 */
@Configuration
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
open class I18nWebConfiguration(
    @Autowired
    private val localeChangeInterceptor: LocaleChangeInterceptor
) : WebMvcConfigurer {

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        // Add the locale change interceptor to the registry
        log.info("注册语言切换拦截器...")
        registry.addInterceptor(localeChangeInterceptor)
    }
}