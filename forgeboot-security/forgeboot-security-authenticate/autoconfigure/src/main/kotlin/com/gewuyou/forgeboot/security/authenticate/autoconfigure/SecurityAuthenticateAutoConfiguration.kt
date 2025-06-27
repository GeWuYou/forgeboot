package com.gewuyou.forgeboot.security.authenticate.autoconfigure

import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.customizer.LoginFilterCustomizer
import com.gewuyou.forgeboot.security.authenticate.api.customizer.OrderedLoginFilterCustomizer
import com.gewuyou.forgeboot.security.authenticate.impl.filter.UnifiedAuthenticationFilter
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeLogoutSuccessHandler
import com.gewuyou.forgeboot.security.core.common.extension.cleanUnNeedConfig
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 * 安全身份验证自动配置
 *
 * 主要用于配置与安全认证相关的 Bean 和 Spring Security 的过滤器链。
 *
 * @since 2025-06-11 15:04:58
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecurityAuthenticateProperties::class)
class SecurityAuthenticateAutoConfiguration {

    /**
     * Spring Security 认证流程的自动配置类。
     *
     * 该内部类负责构建主要的安全过滤器链，处理登录、登出以及异常入口点等核心功能。
     *
     * @property logoutSuccessHandler 处理登出成功的处理器
     * @property authenticationExceptionHandler 处理认证异常的入口点
     * @property unifiedAuthenticationFilter 统一的身份验证过滤器
     * @property customizers 可选的登录过滤器定制器集合
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(SecurityFilterChain::class)
    class SpringSecurityAuthenticateAutoConfiguration(
        private val logoutSuccessHandler: CompositeLogoutSuccessHandler,
        private val authenticationExceptionHandler: AuthenticationEntryPoint,
        private val unifiedAuthenticationFilter: UnifiedAuthenticationFilter,
        private val customizers: ObjectProvider<List<LoginFilterCustomizer>>,
    ) {

        /**
         * 构建并返回一个自定义的安全过滤器链。
         *
         * 该方法配置了：
         * - 清除不必要地默认安全配置
         * - 登出功能及其成功处理器
         * - 异常处理（认证失败入口点）
         * - 请求匹配规则和访问控制策略
         * - 添加统一的身份验证过滤器到过滤器链中
         * - 对可排序的登录过滤器定制器进行排序并应用
         *
         * @param http HttpSecurity 实例，用于构建安全过滤器链
         * @param properties 安全认证属性配置对象
         * @return 构建完成的 SecurityFilterChain 实例
         */
        @Bean(name = ["forgebootSecurityAuthenticationChain"])
        fun loginFilterChain(
            http: HttpSecurity,
            properties: SecurityAuthenticateProperties,
        ): SecurityFilterChain {
            return http
                .cleanUnNeedConfig()
                .logout {
                    it.logoutUrl(properties.logoutUrl)
                        .logoutSuccessHandler(logoutSuccessHandler)
                }
                .exceptionHandling {
                    it.authenticationEntryPoint(authenticationExceptionHandler)
                }
                .securityMatcher(AntPathRequestMatcher(properties.baseUrl))
                .addFilterBefore(unifiedAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                .also {
                    customizers.ifAvailable?.sortedBy { c -> (c as? OrderedLoginFilterCustomizer)?.order ?: 0 }
                        ?.forEach { it.customize(http) }
                }
                .build()
        }
    }
}