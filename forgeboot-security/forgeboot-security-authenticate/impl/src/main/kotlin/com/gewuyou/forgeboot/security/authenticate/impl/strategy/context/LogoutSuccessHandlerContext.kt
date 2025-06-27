package com.gewuyou.forgeboot.security.authenticate.impl.strategy.context

import com.gewuyou.forgeboot.security.authenticate.api.strategy.LogoutSuccessStrategy
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 *注销成功处理程序上下文
 *
 * @since 2025-06-11 00:12:33
 * @author gewuyou
 */
class LogoutSuccessHandlerContext(
    strategies: List<LogoutSuccessStrategy>,
) : AbstractHandlerContext<LogoutSuccessStrategy, LogoutSuccessHandler>(
    strategies,
    "登出处理器",
    { it.supportedLoginTypes() },
    { it }
)