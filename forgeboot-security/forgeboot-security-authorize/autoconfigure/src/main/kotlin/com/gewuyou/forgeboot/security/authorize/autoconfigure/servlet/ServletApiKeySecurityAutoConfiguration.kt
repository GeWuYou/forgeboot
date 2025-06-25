package com.gewuyou.forgeboot.security.authorize.autoconfigure.servlet

import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.impl.servlet.customizer.ApiKeyHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.authorize.impl.servlet.filter.ApiKeyAuthenticationFilter
import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.registrar.SecurityFilterChainRegistrar
import jakarta.servlet.Filter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher

/**
 * API 密钥安全自动配置类，用于在 Servlet Web 应用中自动装配与 API 密钥相关的安全组件。
 *
 * 此配置仅在以下条件下生效：
 * 1. 应用类型为 Servlet；
 * 2. 配置项 "forgeboot.security.authorize.api-key.enabled" 为 true。
 *
 * @property securityAuthorizeProperties 安全授权配置属性，用于获取 API 密钥相关路径等信息
 * @since 2025-06-25 13:41:56
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = "forgeboot.security.authorize.api-key",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ServletApiKeySecurityAutoConfiguration(
    private val securityAuthorizeProperties: SecurityAuthorizeProperties,
) {
    /**
     * 创建 API 密钥认证过滤器 Bean。
     *
     * 该方法在 Spring 容器中尚不存在同名 Bean 的情况下，
     * 构建一个使用指定认证管理器的 ApiKeyAuthenticationFilter 实例。
     *
     * @param authenticationManager 认证管理器，用于执行认证逻辑
     * @return 返回构建完成的 ApiKeyAuthenticationFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = ["apiKeyAuthenticationFilter"])
    fun apiKeyAuthenticationFilter(
        authenticationManager: AuthenticationManager,
    ): Filter {
        return ApiKeyAuthenticationFilter(authenticationManager)
    }

    /**
     * 提供一个用于自定义 HTTP 安全配置的 API 密钥安全自定义器 Bean。
     *
     * 如果容器中尚不存在同名 Bean，则创建并返回 ApiKeyHttpSecurityCustomizer 实例。
     * 此自定义器将注入的认证提供者和过滤器用于构建定制化的安全配置。
     *
     * @param apiKeyAuthenticationProvider 注入已配置的认证提供者，用于安全链构建
     * @param apiKeyAuthenticationFilter   注入已配置的认证过滤器，用于请求处理
     * @return 返回 HttpSecurityCustomizer 的具体实现对象
     */
    @Bean
    @ConditionalOnMissingBean
    fun apiKeyHttpSecurityCustomizer(
        @Qualifier("apiKeyAuthenticationFilter")
        apiKeyAuthenticationFilter: Filter,
    ): HttpSecurityCustomizer {
        return ApiKeyHttpSecurityCustomizer(apiKeyAuthenticationFilter)
    }

    /**
     * 创建默认的安全过滤链，适用于 Servlet 编程模型。
     *
     * 此方法基于配置的路径模式构建一个复合请求匹配器，并通过注册器创建对应的安全过滤链。
     * 过滤链根据配置决定是否使用授权管理器进行访问控制。
     *
     * @param registrar 安全过滤链注册器，用于构建和管理过滤链
     * @param http      Spring Security 的 HttpSecurity 配置对象
     * @param authorizeManager 授权管理器，用于在启用授权管理时定义访问策略
     * @return 构建完成的安全过滤链实例
     */
    @Bean(name = ["defaultApiKeySecurityFilterChain"])
    fun defaultApiKeySecurityFilterChain(
        registrar: SecurityFilterChainRegistrar,
        http: HttpSecurity,
        authorizeManager: AuthorizationManager<RequestAuthorizationContext>
    ): SecurityFilterChain {
        // 从配置中获取 API 密钥适用的路径模式（如：["/api/**", "/open/**"]）
        val patterns = securityAuthorizeProperties.apiKey.pathPatterns

        // 将每个路径模式转换为 AntPathRequestMatcher 实例
        val matchers = patterns.map { AntPathRequestMatcher(it) }

        // 使用 OrRequestMatcher 组合所有路径匹配规则，实现多路径匹配支持
        val combinedMatcher = OrRequestMatcher(matchers)

        // 调用注册器构建安全链，指定链 ID、HttpSecurity 对象和请求匹配器
        return registrar.buildChain(
            SecurityConstants.API_KEY_CHAIN_ID,
            http,
            combinedMatcher
        ) { config ->
            if (securityAuthorizeProperties.apiKey.useAuthorizationManager) {
                // 启用授权管理器时，配置请求通过指定的 authorizeManager 进行访问控制
                config.authorizeHttpRequests {
                    it.anyRequest().access(authorizeManager)
                }
            } else {
                // 禁用授权管理器时，要求所有请求必须经过身份验证
                config.authorizeHttpRequests {
                    it.anyRequest().authenticated()
                }
            }
        }
    }
}