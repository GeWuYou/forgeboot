package com.gewuyou.forgeboot.security.authorize.autoconfigure.servlet

import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.impl.servlet.customizer.SingleTokenHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.authorize.impl.servlet.filter.SingleTokenAuthenticationFilter
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
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher

/**
 * Single Token安全自动配置类，用于在 Servlet Web 应用中自动装配与 Single Token相关的安全组件。
 *
 * 此配置仅在以下条件下生效：
 * 1. 应用类型为 Servlet；
 * 2. 配置项 "forgeboot.security.authorize.single-token.enabled" 为 true。
 *
 * @property securityAuthorizeProperties 安全授权配置属性，用于获取 Single Token相关路径等信息
 * @since 2025-06-25 13:41:56
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = "forgeboot.security.authorize.single-token",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ServletSingleTokenSecurityAutoConfiguration(
    private val securityAuthorizeProperties: SecurityAuthorizeProperties,
) {
    /**
     * 创建 Single Token认证过滤器 Bean。
     *
     * 构建一个 SingleTokenAuthenticationFilter 实例作为 Spring Bean，
     * 该过滤器用于处理基于 Single Token 的身份验证逻辑。
     *
     * @return 返回构建完成的 Filter 接口实现对象（即 SingleTokenAuthenticationFilter）
     */
    @Bean
    @ConditionalOnMissingBean(name = ["singleTokenAuthenticationFilter"])
    fun singleTokenAuthenticationFilter(): Filter {
        return SingleTokenAuthenticationFilter()
    }

    /**
     * 提供一个用于自定义 HTTP 安全配置的 Single Token安全自定义器 Bean。
     *
     * 如果容器中尚不存在同名 Bean，则创建并返回 SingleTokenHttpSecurityCustomizer 实例。
     * 此自定义器将注入的认证过滤器用于构建定制化的安全配置，实现对 HttpSecurity 的扩展。
     *
     * @param singleTokenAuthenticationFilter 注入已配置的认证过滤器，用于请求处理
     * @return 返回 HttpSecurityCustomizer 的具体实现对象
     */
    @Bean
    @ConditionalOnMissingBean
    fun singleTokenHttpSecurityCustomizer(
        @Qualifier("singleTokenAuthenticationFilter")
        singleTokenAuthenticationFilter: Filter,
    ): HttpSecurityCustomizer {
        return SingleTokenHttpSecurityCustomizer(singleTokenAuthenticationFilter)
    }

    /**
     * 创建默认的安全过滤链，适用于 Servlet 编程模型。
     *
     * 此方法基于配置的路径模式构建一个复合请求匹配器，并通过注册器创建对应的 SingleToken 安全过滤链。
     * 过滤链根据配置决定是否使用授权管理器进行访问控制，提供两种模式：
     * - 使用授权管理器时：所有请求通过指定的 authorizeManager 进行访问控制
     * - 不使用授权管理器时：要求所有请求必须经过身份验证
     *
     * @param registrar 安全过滤链注册器，用于构建和管理过滤链
     * @param http      Spring Security 的 HttpSecurity 配置对象
     * @param authorizeManager 授权管理器，用于在启用授权管理时定义访问策略
     * @return 构建完成的安全过滤链实例
     */
    @Bean(name = ["defaultSingleTokenSecurityFilterChain"])
    fun defaultSingleTokenSecurityFilterChain(
        registrar: SecurityFilterChainRegistrar,
        http: HttpSecurity,
        authorizeManager: AuthorizationManager<RequestAuthorizationContext>
    ): SecurityFilterChain {
        // 从配置中获取 singleToken 适用的路径模式（如：["/api/**", "/open/**"]）
        val patterns = securityAuthorizeProperties.singleToken.pathPatterns

        /**
         * 将每个路径模式转换为 AntPathRequestMatcher 实例
         */
        val matchers = patterns.map { AntPathRequestMatcher(it) }

        /**
         * 使用 OrRequestMatcher 组合所有路径匹配规则，实现多路径匹配支持
         */
        val combinedMatcher = OrRequestMatcher(matchers)

        /**
         * 调用注册器构建安全链，指定链 ID、HttpSecurity 对象和请求匹配器
         */
        return registrar.buildChain(
            SecurityConstants.API_KEY_CHAIN_ID,
            http,
            combinedMatcher
        ) { config ->
            if (securityAuthorizeProperties.singleToken.useAuthorizationManager) {
                /**
                 * 启用授权管理器时，配置请求通过指定的 authorizeManager 进行访问控制
                 */
                config.authorizeHttpRequests {
                    it.anyRequest().access(authorizeManager)
                }
            } else {
                /**
                 * 禁用授权管理器时，要求所有请求必须经过身份验证
                 */
                config.authorizeHttpRequests {
                    it.anyRequest().authenticated()
                }
            }
        }
    }
}