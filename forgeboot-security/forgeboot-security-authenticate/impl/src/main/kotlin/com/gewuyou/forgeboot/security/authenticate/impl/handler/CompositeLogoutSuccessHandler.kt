package com.gewuyou.forgeboot.security.authenticate.impl.handler

import com.gewuyou.forgeboot.security.authenticate.impl.resolver.CompositeLoginRequestResolver
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.context.LogoutSuccessHandlerContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 * 组合注销成功处理程序
 *
 * 该类通过组合多种注销成功处理器实现统一的注销成功处理逻辑。
 *
 * @property resolver 用于解析请求并确定适用的注销成功处理器的解析器实例
 * @property context 提供具体地注销成功处理上下文，用于动态获取处理器
 *
 * @since 2025-06-11 00:13:04
 * @author gewuyou
 */
class CompositeLogoutSuccessHandler(
    private val resolver: CompositeLoginRequestResolver,
    private val context: LogoutSuccessHandlerContext,
) : AbstractDelegatingHandler<LogoutSuccessHandler>(
    resolver, context::resolve
), LogoutSuccessHandler {

    /**
     * 在用户成功注销时执行的具体操作
     *
     * 该方法通过解析当前请求获取对应的注销成功处理器，并委托其执行实际的注销后操作。
     *
     * @param request HTTP请求对象，提供客户端发送的请求信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @param authentication 包含认证信息的对象，在注销时通常表示已认证的用户信息
     */
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        resolveDelegate(request).onLogoutSuccess(request, response, authentication)
    }
}