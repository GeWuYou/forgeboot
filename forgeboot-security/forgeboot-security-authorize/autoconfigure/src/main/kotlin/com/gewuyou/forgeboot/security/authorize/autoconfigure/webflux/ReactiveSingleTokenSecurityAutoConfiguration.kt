package com.gewuyou.forgeboot.security.authorize.autoconfigure.webflux

import com.gewuyou.forgeboot.security.authorize.api.core.config.SecurityAuthorizeProperties
import com.gewuyou.forgeboot.security.authorize.impl.webflux.customizer.SingleTokenServerHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.authorize.impl.webflux.filter.ReactiveSingleTokenAuthenticationFilter
import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.customizer.ServerHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.registrar.SecurityWebFilterChainRegistrar
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.WebFilter

/**
 * 配置类，用于在满足条件时自动配置基于 Single Token 的反应式安全机制。
 *
 * 仅在满足以下条件时生效：
 * - 应用类型为 REACTIVE（反应式应用）
 * - 配置项 `forgeboot.security.authorize.single-token.enabled` 被设置为 true
 *
 * @property securityAuthorizeProperties 安全授权配置属性，用于获取 Single Token 的路径匹配规则等信息
 * @since 2025-06-25 21:04:37
 * @author gewuyou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
    prefix = "forgeboot.security.authorize.single-token",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ReactiveSingleTokenSecurityAutoConfiguration(
    private val securityAuthorizeProperties: SecurityAuthorizeProperties
) {

    /**
     * 创建并注册名为 reactiveSingleTokenAuthenticationFilter 的 WebFilter Bean。
     *
     * 该过滤器负责处理基于 Single Token 的认证逻辑，确保请求携带有效的 Token。
     * 仅当容器中尚未存在同名的 Bean 时才会创建此实例。
     *
     * @param reactiveAuthenticationManager 反应式认证管理器，用于执行认证操作
     * @return 构建完成的 WebFilter 实例
     */
    @Bean
    @ConditionalOnMissingBean(name = ["reactiveSingleTokenAuthenticationFilter"])
    fun singleTokenAuthenticationFilter(
        reactiveAuthenticationManager: ReactiveAuthenticationManager
    ): WebFilter {
        return ReactiveSingleTokenAuthenticationFilter(reactiveAuthenticationManager)
    }

    /**
     * 创建并注册 ServerHttpSecurityCustomizer 的 Bean，用于定制 Single Token 安全配置。
     *
     * 此自定义器用于在构建 Spring Security 过滤器链时插入 Single Token 认证逻辑。
     * 仅当容器中尚未存在相同类型的 Bean 时才会创建此实例。
     *
     * @param reactiveSingleTokenAuthenticationFilter 使用指定名称从 Spring 容器中注入的 Single Token 反应式认证过滤器 Bean。
     *                                                该过滤器负责处理实际的 Single Token 认证逻辑。
     *
     * @return 构建完成的 ServerHttpSecurityCustomizer 实例，用于在安全配置中添加 Single Token 相关逻辑。
     */
    @Bean
    @ConditionalOnMissingBean
    fun singleTokenServerHttpSecurityCustomizer(
        @Qualifier("reactiveSingleTokenAuthenticationFilter")
        reactiveSingleTokenAuthenticationFilter: WebFilter,
    ): ServerHttpSecurityCustomizer {
        return SingleTokenServerHttpSecurityCustomizer(reactiveSingleTokenAuthenticationFilter)
    }

    /**
     * 创建并注册基于 Single Token 认证的 WebFlux 安全过滤器链。
     *
     * 该方法利用 SecurityWebFilterChainRegistrar 注册一个具有路径匹配规则的安全过滤器链，
     * 仅对符合配置中指定路径模式的请求生效，并要求通过 Single Token 认证。
     *
     * @param registrar 用于注册和构建安全过滤器链的核心工具类，负责链的组装过程。
     * @param http Spring Security 提供的 ServerHttpSecurity 实例，用于构建 HTTP 安全配置。
     * @param reactiveAuthorizationManager 反应式授权管理器，用于在使用授权管理逻辑时提供访问控制。
     * @return 构建完成的 SecurityWebFilterChain 实例，表示定义好的安全过滤器链。
     *
     * 关键逻辑总结：
     * 1. 获取配置中的路径模式列表 patterns；
     * 2. 将每个路径转换为 ServerWebExchangeMatcher 实例，形成 matchers 列表；
     * 3. 合并所有 matcher 形成 combinedMatcher 复合匹配规则；
     * 4. 利用 registrar 构建过滤器链，并根据 useAuthorizationManager 配置决定采用何种认证方式：
     *    - 若启用 authorizationManager，则通过 access 方法设置自定义的授权逻辑；
     *    - 否则直接要求请求必须经过认证。
     */
    @Bean(name = ["defaultSingleTokenSecurityWebFilterChain"])
    fun defaultSingleTokenSecurityWebFilterChain(
        registrar: SecurityWebFilterChainRegistrar,
        http: ServerHttpSecurity,
        reactiveAuthorizationManager: ReactiveAuthorizationManager<AuthorizationContext>
    ): SecurityWebFilterChain {
        val patterns = securityAuthorizeProperties.singleToken.pathPatterns
        val matchers = patterns.map { PathPatternParserServerWebExchangeMatcher(it) }
        val combinedMatcher: ServerWebExchangeMatcher =
            ServerWebExchangeMatchers.matchers(*matchers.toTypedArray())
        return registrar.buildChain(
            SecurityConstants.SINGLE_TOKEN_CHAIN_ID,
            http,
            combinedMatcher
        ) { config ->
            if (securityAuthorizeProperties.singleToken.useAuthorizationManager) {
                config.authorizeExchange {
                    it.anyExchange().access(reactiveAuthorizationManager)
                }
            } else {
                config.authorizeExchange {
                    it.anyExchange().authenticated()
                }
            }

        }
    }
}