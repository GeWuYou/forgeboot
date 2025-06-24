package com.gewuyou.forgeboot.security.authenticate.impl.handler

import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationFailureHandlerContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.AuthenticationFailureHandler

/**
 * 组合身份验证故障处理程序
 *
 * 该类通过组合不同的身份验证失败处理器来处理认证失败场景，利用解析器和上下文策略来动态选择实际的处理器。
 *
 * @property resolver 用于解析请求并确定适用的身份验证失败处理器的组件
 * @property context 提供身份验证失败处理策略的上下文对象
 *
 * @since 2025-06-11 00:11:52
 * @author gewuyou
 */
class CompositeAuthenticationFailureHandler(
    private val resolver: CompositeLoginRequestResolver,
    private val context: AuthenticationFailureHandlerContext,
) : AbstractDelegatingHandler<AuthenticationFailureHandler>(
    resolver, context::resolve
), AuthenticationFailureHandler {

    /**
     * 在身份验证失败时调用的方法
     *
     * 此方法会解析出适用于当前请求的具体身份验证失败处理器，并委托其进行失败处理。
     *
     * @param request 来自客户端的 HTTP 请求
     * @param response 发送回客户端的 HTTP 响应
     * @param exception 身份验证过程中抛出的异常信息
     */
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: org.springframework.security.core.AuthenticationException,
    ) {
        resolveDelegate(request).onAuthenticationFailure(request, response, exception)
    }
}