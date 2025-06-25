package com.gewuyou.forgeboot.security.authorize.impl.webflux.registrar

import com.gewuyou.forgeboot.security.core.common.customizer.ServerHttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.extension.cleanUnNeedConfig
import com.gewuyou.forgeboot.security.core.common.registrar.SecurityWebFilterChainRegistrar
import com.gewuyou.forgeboot.security.core.common.wrapper.SecurityWebFilterChainWrapper
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

/**
 * 无状态安全 Web 过滤器链注册商
 *
 * 用于创建和管理无状态认证场景下的安全过滤器链。
 *
 * @property serverHttpSecurityCustomizer 安全配置定制器列表，按需对安全配置进行个性化调整
 * @since 2025-06-24 12:40:01
 * @author gewuyou
 */
class StatelessSecurityWebFilterChainRegistrar(
    private val serverHttpSecurityCustomizer: List<ServerHttpSecurityCustomizer>,
    private val reactiveAuthorizationManager: ReactiveAuthorizationManager<AuthorizationContext>,
    private val reactiveAccessDeniedHandler: ServerAccessDeniedHandler,
) : SecurityWebFilterChainRegistrar {
    /**
     * 构建安全过滤器链
     *
     * 此方法基于传入的匹配器和配置逻辑创建一个定制化的安全策略，并将其封装为可管理的安全过滤器链实例。
     * 方法内部主要完成基础安全配置清理、请求匹配规则设置、权限验证管理、异常处理机制以及自定义配置扩展，
     * 最终构建并返回封装好的安全过滤器链对象。
     *
     * @param chainId 唯一标识此过滤器链的字符串，用于后续引用或管理
     * @param http 提供基础配置的 ServerHttpSecurity 对象，用于构建安全策略
     * @param matcher 用于匹配请求的 ServerWebExchangeMatcher 对象
     * @param config 用于配置安全策略的函数，接受 ServerHttpSecurity 对象作为参数
     * @return 返回构建完成的 SecurityWebFilterChainWrapper 实例，包含已配置的安全策略及链标识
     */
    override fun buildChain(
        chainId: String,
        http: ServerHttpSecurity,
        matcher: ServerWebExchangeMatcher,
        config: (ServerHttpSecurity) -> Unit,
    ): SecurityWebFilterChainWrapper {
        /**
         * 安全配置流程：
         * 1. 清理不必要且默认的安全配置；
         * 2. 设置请求匹配规则；
         * 3. 配置访问控制策略（允许 OPTIONS 请求并限制其他请求）；
         * 4. 设置访问被拒绝时的异常处理器；
         * 5. 应用额外的自定义配置。
         */
        http
            .cleanUnNeedConfig()
            .securityMatcher(matcher)
            .authorizeExchange {
                /**
                 * 显式允许所有 OPTIONS 预检请求，确保跨域请求能正常进行预检。
                 */
                it.matchers(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"))
                    .permitAll()
                /**
                 * 所有其他类型的请求都需要通过 reactiveAuthorizationManager 进行访问控制。
                 */
                it
                    .anyExchange()
                    .access(reactiveAuthorizationManager)
            }
            .exceptionHandling {
                /**
                 * 设置全局访问被拒绝处理器，用于统一处理无权访问的请求。
                 */
                it.accessDeniedHandler(reactiveAccessDeniedHandler)
            }
            .also { config(it) }

        /**
         * 筛选出支持当前 chainId 的定制器，并依次对当前 http 配置进行扩展定制，
         * 实现更细粒度的链级安全策略调整。
         */
        serverHttpSecurityCustomizer.filter { it.supports(chainId) }.forEach { it.customize(http) }

        /**
         * 使用构建完成的 SecurityWebFilterChain 实例和链 ID 创建包装对象并返回，
         * 便于上层组件管理和使用该安全链。
         */
        return SecurityWebFilterChainWrapper(
            http.build(),
            chainId
        )
    }
}