package com.gewuyou.forgeboot.security.authorize.impl.builder

import com.gewuyou.forgeboot.security.core.common.builder.SecurityFilterChainRegistrar
import com.gewuyou.forgeboot.security.core.common.customizer.HttpSecurityCustomizer
import com.gewuyou.forgeboot.security.core.common.extension.cleanUnNeedConfig
import com.gewuyou.forgeboot.security.core.common.wrapper.SecurityFilterChainWrapper
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * 无状态安全过滤器链注册商
 *
 * 此类用于注册和构建无状态的安全过滤器链，适用于基于令牌的身份验证场景。
 * 它通过定制 HttpSecurity 配置来实现特定的访问控制策略。
 *
 * @property httpSecurityCustomizer 提供针对特定链的自定义配置逻辑
 * @property authorizationManager 负责处理请求的授权逻辑
 * @property accessDeniedHandler 处理未授权访问时的响应逻辑
 * @since 2025-06-24 16:14:55
 * @author gewuyou
 */
class StatelessSecurityFilterChainRegistrar(
    private val httpSecurityCustomizer: List<HttpSecurityCustomizer>,
    private val authorizationManager: AuthorizationManager<RequestAuthorizationContext>,
    private val accessDeniedHandler: AccessDeniedHandler,
) : SecurityFilterChainRegistrar {
    /**
     * 构建安全过滤器链
     *
     * 此方法用于基于提供的匹配器和配置逻辑创建一个定制化的安全过滤器链。
     *
     * @param chainId 唯一标识此过滤器链的字符串，用于后续引用或管理
     * @param http 提供基础配置的 HttpSecurity 对象，用于构建 Web 安全策略
     * @param matcher 用于匹配请求的 RequestMatcher 对象，决定是否应用该过滤器链
     * @param config 用于配置安全策略的函数，接受 ServerHttpSecurity 对象作为参数进行自定义配置
     * @return 返回构建完成的 SecurityWebFilterChainWrapper 实例，封装了最终的安全过滤器链
     */
    override fun buildChain(
        chainId: String,
        http: HttpSecurity,
        matcher: RequestMatcher,
        config: (HttpSecurity) -> Unit,
    ): SecurityFilterChainWrapper {
        // 清除不必要的默认安全配置，优化无状态场景下的行为
        // 设置请求匹配器以确定过滤器链适用范围
        // 配置 HTTP 请求的授权规则：
        // - 允许所有 OPTIONS 请求（通常用于跨域预检）
        // - 所有其他请求都需要通过授权管理器验证
        // 配置异常处理逻辑，使用指定的访问拒绝处理器
        // 应用额外的自定义配置闭包
        http
            .cleanUnNeedConfig()
            .securityMatcher(matcher)
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS).permitAll()
                it.anyRequest().access(authorizationManager)
            }
            .exceptionHandling { it.accessDeniedHandler(accessDeniedHandler) }
            .also { config(it) }

        // 过滤出支持当前链 ID 的定制器并依次应用它们的自定义逻辑
        httpSecurityCustomizer.filter { it.supports(chainId) }.forEach { it.customize(http) }

        // 构建并返回封装好的安全过滤器链包装对象
        return SecurityFilterChainWrapper(http.build(), chainId)
    }
}