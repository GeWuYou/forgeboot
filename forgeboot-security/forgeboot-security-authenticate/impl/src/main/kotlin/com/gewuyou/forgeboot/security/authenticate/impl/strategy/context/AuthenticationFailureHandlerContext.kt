package com.gewuyou.forgeboot.security.authenticate.impl.strategy.context

import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationFailureStrategy
import org.springframework.security.web.authentication.AuthenticationFailureHandler

/**
 *身份验证故障处理程序上下文
 *
 * @since 2025-06-10 23:48:02
 * @author gewuyou
 */
class AuthenticationFailureHandlerContext(
    strategies: List<AuthenticationFailureStrategy>
) : AbstractHandlerContext<AuthenticationFailureStrategy, AuthenticationFailureHandler>(
    strategies,
    "认证失败处理器",
    { it.supportedLoginType() },
    { it }
)