package com.gewuyou.forgeboot.security.authenticate.api.strategy

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 * 注销成功策略接口，用于定义在用户注销成功后的处理逻辑。
 *
 * 实现此类的策略应当提供具体地登出后操作，例如清理会话、记录日志等。
 *
 * @since 2025-06-10 23:49:35
 * @author gewuyou
 */
interface LogoutSuccessStrategy : AuthenticationHandlerSupportStrategy, LogoutSuccessHandler