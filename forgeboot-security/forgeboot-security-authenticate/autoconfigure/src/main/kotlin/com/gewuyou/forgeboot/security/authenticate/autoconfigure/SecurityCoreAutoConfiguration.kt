package com.gewuyou.forgeboot.security.authenticate.autoconfigure

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.registry.LoginRequestTypeRegistry
import com.gewuyou.forgeboot.security.authenticate.api.resolver.LoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.api.service.UserCacheService
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationFailureStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LogoutSuccessStrategy
import com.gewuyou.forgeboot.security.authenticate.impl.filter.UnifiedAuthenticationFilter
import com.gewuyou.forgeboot.security.authenticate.impl.handler.AuthenticationExceptionHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeAuthenticationFailureHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeAuthenticationSuccessHandler
import com.gewuyou.forgeboot.security.authenticate.impl.handler.CompositeLogoutSuccessHandler
import com.gewuyou.forgeboot.security.authenticate.impl.registry.SimpleLoginRequestTypeRegistry
import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationFailureHandlerContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationSuccessHandlerContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LoginRequestConverterContext
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LogoutSuccessHandlerContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 *安全核心自动配置
 *
 * @since 2025-06-27 07:54:04
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
class SecurityCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun bCryptPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @ConditionalOnMissingBean
    fun nullUserCacheService(): UserCacheService {
        return object : UserCacheService {
            override fun getUserFromCache(principal: String): UserDetails? {
                return null
            }

            override fun putUserToCache(userDetails: UserDetails) {
                //ignore
            }

            override fun removeUserFromCache(principal: String) {
                //ignore
            }
        }
    }

    /**
     * 创建并返回一个登录请求类型注册器。
     *
     * 用于注册和管理不同类型的登录请求，确保系统中所有登录方式能够被统一识别和处理。
     * 如果上下文中不存在该类型的 Bean，则会使用默认实现 SimpleLoginRequestTypeRegistry。
     *
     * @return 实现 LoginRequestTypeRegistry 接口的 Bean 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun loginRequestTypeRegistry(): LoginRequestTypeRegistry {
        return SimpleLoginRequestTypeRegistry()
    }

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