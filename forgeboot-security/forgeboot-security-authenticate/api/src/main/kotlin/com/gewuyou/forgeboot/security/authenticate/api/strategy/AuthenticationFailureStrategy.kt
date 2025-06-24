package com.gewuyou.forgeboot.security.authenticate.api.strategy

import org.springframework.security.web.authentication.AuthenticationFailureHandler

/**
 * 身份验证失败策略接口，用于定义不同登录类型的身份验证失败处理机制。
 *
 * @since 2025-06-10 23:43:43
 * @author gewuyou
 */
interface AuthenticationFailureStrategy : AuthenticationHandlerSupportStrategy, AuthenticationFailureHandler
