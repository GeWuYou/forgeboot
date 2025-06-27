package com.gewuyou.forgeboot.security.authenticate.impl.strategy.context

import com.gewuyou.forgeboot.security.authenticate.api.strategy.AuthenticationSuccessStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

/**
 *身份验证成功处理程序上下文
 *
 * @since 2025-06-10 23:45:40
 * @author gewuyou
 */
class AuthenticationSuccessHandlerContext(
    strategies: List<AuthenticationSuccessStrategy>,
) : AbstractHandlerContext<AuthenticationSuccessStrategy, AuthenticationSuccessHandler>(
    strategies,
    "认证成功处理器"
    ,
    { it.supportedLoginTypes() },
    { it }
)