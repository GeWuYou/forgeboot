package com.gewuyou.forgeboot.security.authenticate.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.customizer.LoginFilterCustomizer
import com.gewuyou.forgeboot.security.authenticate.api.resolver.LoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationFailureStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LogoutSuccessStrategy
import com.gewuyou.forgeboot.security.core.common.extension.cleanUnNeedConfig
import com.gewuyou.forgeboot.security.authenticate.impl.filter.UnifiedAuthenticationFilter
import com.gewuyou.forgeboot.security.authenticate.impl.handler.AuthenticationExceptionHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeAuthenticationFailureHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeAuthenticationSuccessHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeLogoutSuccessHandler
import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationFailureHandlerContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationSuccessHandlerContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LoginRequestConverterContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LogoutSuccessHandlerContext
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
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
@Configuration
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

    /**
     * ForgeBoot安全组件的自动配置类。
     *
     * 负责注册多个用于处理身份验证流程的组合式组件，如解析器、上下文、处理器等。
     */
    @Configuration(proxyBeanMethods = false)
    class ForgeBootSecurityAuthenticateAutoConfiguration {

        /**
         * 创建并返回一个组合式的登录请求解析器。
         *
         * 将多个 LoginRequestResolver 实现组合成一个统一的接口调用入口。
         *
         * @param resolvers 所有可用的 LoginRequestResolver 实例列表
         * @return 组合后的 CompositeLoginRequestResolver 实例
         */
        @Bean
        fun compositeLoginRequestResolver(
            resolvers: List<LoginRequestResolver>,
        ) = CompositeLoginRequestResolver(resolvers)

        /**
         * 创建并返回一个登出成功处理的上下文实例。
         *
         * 用于根据注册的策略动态选择合适的 LogoutSuccessStrategy。
         *
         * @param strategies 所有可用的 LogoutSuccessStrategy 实例列表
         * @return 初始化好的 LogoutSuccessHandlerContext 实例
         */
        @Bean
        fun logoutSuccessHandlerContext(
            strategies: List<LogoutSuccessStrategy>,
        ) = LogoutSuccessHandlerContext(strategies)

        /**
         * 创建并返回一个组合式的登出成功处理器。
         *
         * 使用解析器和上下文来决定最终使用的登出成功策略。
         *
         * @param resolver 登录请求解析器
         * @param context 登出成功处理上下文
         * @return 初始化好的 CompositeLogoutSuccessHandler 实例
         */
        @Bean(name = ["logoutSuccessHandler"])
        fun compositeLogoutSuccessHandler(
            resolver: CompositeLoginRequestResolver,
            context: LogoutSuccessHandlerContext,
        ) = CompositeLogoutSuccessHandler(resolver, context)

        /**
         * 创建并返回一个认证异常处理器。
         *
         * 当用户未认证或认证失败时，通过此处理器响应客户端。
         *
         * @param objectMapper JSON 序列化工具
         * @param properties 安全认证属性配置
         * @return 初始化好的 AuthenticationExceptionHandler 实例
         */
        @Bean
        fun authenticationExceptionHandler(
            objectMapper: ObjectMapper,
            properties: SecurityAuthenticateProperties,
        ): AuthenticationEntryPoint = AuthenticationExceptionHandler(objectMapper, properties)

        /**
         * 创建并返回一个认证管理器。
         *
         * 使用一组 AuthenticationProvider 来支持多种认证方式。
         *
         * @param authenticationProviders 所有可用的认证提供者
         * @return 初始化好的 ProviderManager 实例
         */
        @Bean(name = ["authenticationManager"])
        @Primary
        fun authenticationManager(
            authenticationProviders: List<AuthenticationProvider>,
        ): AuthenticationManager = ProviderManager(authenticationProviders)

        /**
         * 创建并返回一个认证成功处理的上下文。
         *
         * 根据当前请求上下文动态选择合适的认证成功策略。
         *
         * @param strategies 所有可用的 AuthenticationSuccessStrategy 实例列表
         * @return 初始化好的 AuthenticationSuccessHandlerContext 实例
         */
        @Bean
        fun authenticationSuccessHandlerContext(
            strategies: List<AuthenticationSuccessStrategy>,
        ) = AuthenticationSuccessHandlerContext(strategies)

        /**
         * 创建并返回一个组合式的认证成功处理器。
         *
         * 委托给具体的策略实现来进行响应。
         *
         * @param resolver 登录请求解析器
         * @param context 认证成功处理上下文
         * @return 初始化好的 CompositeAuthenticationSuccessHandler 实例
         */
        @Bean(name = ["authenticationSuccessHandler"])
        fun authenticationSuccessHandler(
            resolver: CompositeLoginRequestResolver,
            context: AuthenticationSuccessHandlerContext,
        ): AuthenticationSuccessHandler = CompositeAuthenticationSuccessHandler(resolver, context)

        /**
         * 创建并返回一个认证失败处理的上下文。
         *
         * 根据当前请求上下文动态选择合适的认证失败策略。
         *
         * @param strategies 所有可用的 AuthenticationFailureStrategy 实例列表
         * @return 初始化好的 AuthenticationFailureHandlerContext 实例
         */
        @Bean
        fun authenticationFailureHandlerContext(
            strategies: List<AuthenticationFailureStrategy>,
        ) = AuthenticationFailureHandlerContext(strategies)

        /**
         * 创建并返回一个组合式的认证失败处理器。
         *
         * 委托给具体的策略实现来进行响应。
         *
         * @param resolver 登录请求解析器
         * @param context 认证失败处理上下文
         * @return 初始化好的 CompositeAuthenticationFailureHandler 实例
         */
        @Bean(name = ["authenticationFailureHandler"])
        fun authenticationFailureHandler(
            resolver: CompositeLoginRequestResolver,
            context: AuthenticationFailureHandlerContext,
        ): AuthenticationFailureHandler = CompositeAuthenticationFailureHandler(resolver, context)

        /**
         * 创建并返回一个登录请求转换上下文。
         *
         * 用于根据当前请求上下文动态选择合适的登录请求转换逻辑。
         *
         * @param applicationContext Spring 应用上下文
         * @return 初始化好的 LoginRequestConverterContext 实例
         */
        @Bean
        fun loginRequestConverterContext(
            applicationContext: ApplicationContext,
        ) = LoginRequestConverterContext(applicationContext)

        /**
         * 创建并返回一个统一的身份验证过滤器。
         *
         * 该过滤器是整个认证流程的核心，处理登录请求并触发相应的成功/失败处理器。
         *
         * @param properties 安全认证属性配置
         * @param authenticationManager 认证管理器
         * @param authenticationSuccessHandler 成功认证处理器
         * @param authenticationFailureHandler 失败认证处理器
         * @param compositeLoginRequestResolver 组合登录请求解析器
         * @param loginRequestConverterContext 登录请求转换上下文
         * @return 初始化好的 UnifiedAuthenticationFilter 实例
         */
        @Bean
        fun unifiedAuthenticationFilter(
            properties: SecurityAuthenticateProperties,
            authenticationManager: AuthenticationManager,
            authenticationSuccessHandler: AuthenticationSuccessHandler,
            authenticationFailureHandler: AuthenticationFailureHandler,
            compositeLoginRequestResolver: CompositeLoginRequestResolver,
            loginRequestConverterContext: LoginRequestConverterContext,
        ): UnifiedAuthenticationFilter =
            UnifiedAuthenticationFilter(
                AntPathRequestMatcher(
                    properties.baseUrl + properties.loginUrl,
                    properties.loginHttpMethod
                ),
                authenticationManager,
                authenticationSuccessHandler,
                authenticationFailureHandler,
                compositeLoginRequestResolver,
                loginRequestConverterContext
            )
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(
        basePackages = [
            "com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl",
            "com.gewuyou.forgeboot.security.authenticate.impl.provider.impl",
        ]
    )
    class ForgeBootDefaultSecurityAuthenticateAutoConfiguration
}

/**
 * 用于支持登录过滤器扩展器排序
 *
 * 通过 order 属性为 LoginFilterCustomizer 提供排序能力，确保其按预期顺序执行。
 */
interface OrderedLoginFilterCustomizer : LoginFilterCustomizer {
    val order: Int
}