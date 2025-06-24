package com.gewuyou.forgeboot.security.authenticate.impl.handler

import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.AuthenticationSuccessHandlerContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

/**
 * 统一身份验证成功处理程序
 *
 * 该类实现了 [AuthenticationSuccessHandler] 接口，用于在身份验证成功后执行相应的处理逻辑。
 * 它通过组合多个具体的处理器来实现灵活的身份验证成功响应策略。
 *
 * @property resolver 用于解析登录请求并确定使用哪个具体的成功处理器
 * @property context 提供处理上下文信息，用于解析和执行成功处理逻辑
 *
 * @since 2025-06-10 23:48:32
 * @author gewuyou
 */
class CompositeAuthenticationSuccessHandler(
    private val resolver: CompositeLoginRequestResolver,
    private val context: AuthenticationSuccessHandlerContext,
) : AbstractDelegatingHandler<AuthenticationSuccessHandler>(
    resolver, context::resolve
), AuthenticationSuccessHandler {
    /**
     * 身份验证成功时的回调方法
     *
     * 此方法会在用户成功通过身份验证后被调用。它使用 [resolveDelegate] 方法根据当前请求解析出
     * 合适的具体处理器，并委托给该处理器执行后续操作。
     *
     * @param request HTTP 请求对象，提供客户端的请求信息
     * @param response HTTP 响应对象，用于向客户端发送响应
     * @param authentication 包含身份验证成功的用户信息和权限等数据的对象
     */
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        resolveDelegate(request).onAuthenticationSuccess(request, response, authentication)
    }
}
