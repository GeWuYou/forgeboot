package com.gewuyou.forgeboot.security.authenticate.impl.filter

import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LoginRequestConverterContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 * 统一的身份验证过滤器
 *
 * 该类实现了身份验证流程的核心逻辑，通过组合不同的解析策略和转换上下文，
 * 来支持多种登录请求的处理方式。
 *
 * @param pathRequestMatcher 请求路径匹配器，用于判断当前请求是否需要处理
 * @param authenticationManager 身份验证管理器，用于执行实际的身份验证操作
 * @param authenticationSuccessHandler 认证成功处理器，用于处理认证成功后的响应
 * @param authenticationFailureHandler 认证失败处理器，用于处理认证失败后的响应
 * @param compositeLoginRequestResolver 组合登录请求解析器，用于解析不同类型的登录请求
 * @param loginRequestConverterContext 登录请求转换上下文，用于选择合适地转换策略
 *
 * @since 2025-06-10 20:28:08
 * @author gewuyou
 */
class UnifiedAuthenticationFilter(
    pathRequestMatcher: AntPathRequestMatcher,
    authenticationManager: AuthenticationManager,
    authenticationSuccessHandler: AuthenticationSuccessHandler,
    authenticationFailureHandler: AuthenticationFailureHandler,
    private val compositeLoginRequestResolver: CompositeLoginRequestResolver,
    private val loginRequestConverterContext: LoginRequestConverterContext,
) : AbstractAuthenticationProcessingFilter(pathRequestMatcher) {

    init {
        // 初始化身份验证管理器、成功处理器和失败处理器
        setAuthenticationManager(authenticationManager)
        setAuthenticationSuccessHandler(authenticationSuccessHandler)
        setAuthenticationFailureHandler(authenticationFailureHandler)
    }

    /**
     * 尝试进行身份验证
     *
     * 此方法负责从请求中提取登录信息，并使用组合解析器和转换上下文来构建认证请求。
     * 最终调用身份验证管理器执行认证操作。
     *
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return 返回经过认证的Authentication对象
     */
    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Authentication {
        // 尝试认证：通过组合解析器解析请求内容，再根据转换上下文选择合适的策略进行认证
        return authenticationManager.authenticate(
            // 通过上下文执行转换策略
            loginRequestConverterContext.executeStrategy(
                // 使用组合解析器解析HTTP请求
                compositeLoginRequestResolver.resolve(request)
            )
        )
    }
}