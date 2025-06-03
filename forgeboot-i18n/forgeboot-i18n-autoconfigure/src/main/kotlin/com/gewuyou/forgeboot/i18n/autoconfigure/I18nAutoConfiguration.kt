package com.gewuyou.forgeboot.i18n.autoconfigure


import com.gewuyou.forgeboot.core.extension.log
import com.gewuyou.forgeboot.i18n.api.MessageResolver
import com.gewuyou.forgeboot.i18n.api.config.I18nProperties
import com.gewuyou.forgeboot.i18n.impl.filter.ReactiveLocaleResolver
import com.gewuyou.forgeboot.i18n.impl.resolver.I18nMessageResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.util.StringUtils
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.io.IOException
import java.util.*

/**
 * 本地化配置
 *
 * @author gewuyou
 * @since 2024-11-11 00:46:01
 */
@Configuration
@EnableConfigurationProperties(I18nProperties::class)
open class I18nAutoConfiguration(
    private val i18nProperties: I18nProperties
) {
    /**
     * 配置并创建一个国际化的消息源
     *
     * 此方法首先会扫描指定路径下所有的国际化属性文件，然后将这些文件路径设置到消息源中
     *
     * @return MessageSource 国际化消息源
     */
    @Bean
    @ConditionalOnMissingBean
    open fun messageSource(): MessageSource {
        log.info("开始加载 I18n 配置...")
        val messageSource = ReloadableResourceBundleMessageSource()
        // 动态扫描所有 i18n 子目录下的 messages.properties 文件
        val baseNames = scanBaseNames(i18nProperties.wildPathForLanguageFiles)

        // 设置文件路径到 messageSource
        messageSource.setBasenames(*baseNames.toTypedArray<String>())
        messageSource.setDefaultEncoding("UTF-8")
        log.info("I18n 配置加载完成...")
        return messageSource
    }

    /**
     * 创建并配置一个国际化的消息解析器
     * 该方法通过Spring的条件注解有选择性地创建一个MessageResolver实例
     * 主要用于解决国际化消息的解析问题
     *
     * @param i18nMessageSource 一个MessageSource实例，用于解析国际化消息
     * @return 返回一个MessageResolver实例，用于在国际化的环境中解析消息
     */
    @Bean
    @ConditionalOnMissingBean(MessageResolver::class)
    open fun i18nMessageResolver(i18nMessageSource: MessageSource): MessageResolver {
        return I18nMessageResolver(i18nMessageSource)
    }


    /**
     * 扫描指定路径下的所有国际化属性文件路径
     *
     * 此方法会根据提供的基础路径，查找所有匹配的国际化属性文件，并将其路径添加到列表中返回
     * 主要用于动态加载项目中的国际化配置文件
     *
     * @param basePath 国际化属性文件所在的基路径
     * @return List<String> 包含所有找到的国际化属性文件路径的列表
     */
    private fun scanBaseNames(basePath: String): List<String> {
        val baseNames: MutableList<String> = ArrayList()
        val suffix = i18nProperties.locationPatternSuffix
        log.info("开始扫描 I18n 文件 {}", basePath)
        try {
            val resources = PathMatchingResourcePatternResolver().getResources(
                "$basePath*$suffix"
            )
            for (resource in resources) {
                val path = resource.uri.toString()
                log.info("找到 I18n 文件路径: {}", path)
                // 转换路径为 Spring 的 basename 格式（去掉 .properties 后缀）
                val baseName = path.substring(0, path.lastIndexOf(suffix))
                if (!baseNames.contains(baseName)) {
                    baseNames.add(baseName)
                }
            }
        } catch (e: IOException) {
            log.error("无法扫描 I18n 文件", e)
        }
        return baseNames
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
    @Bean
    @ConditionalOnClass(name = ["org.springframework.web.servlet.DispatcherServlet"])
    open fun localeResolver(i18nProperties: I18nProperties): LocaleResolver {
        return object : AcceptHeaderLocaleResolver() {
            override fun resolveLocale(request: HttpServletRequest): Locale {
                log.info("开始解析URL参数 lang 语言配置...")
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
     * 创建一个区域设置更改拦截器
     *
     * 此方法在项目为 Servlet 类型且配置了相应的属性时被调用，创建的拦截器用于处理 URL 参数中的语言变更请求
     *
     * @param i18nProperties 本地化属性配置
     * @return LocaleChangeInterceptor 区域设置更改拦截器
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
    open fun localeChangeInterceptor(i18nProperties: I18nProperties): LocaleChangeInterceptor {
        log.info("创建区域设置更改拦截器...")
        val interceptor = LocaleChangeInterceptor()
        // 设置 URL 参数名，例如 ?lang=en 或 ?lang=zh
        interceptor.paramName = i18nProperties.langRequestParameter
        return interceptor
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
    open fun createReactiveLocaleResolver(i18nProperties: I18nProperties): ReactiveLocaleResolver {
        log.info("创建 WebFlux 区域设置解析器...")
        return ReactiveLocaleResolver(i18nProperties)
    }
}
