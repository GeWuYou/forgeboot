package com.gewuyou.forgeboot.security.authenticate.api.strategy

import org.springframework.security.web.authentication.AuthenticationSuccessHandler

/**
 * 身份验证成功策略接口，用于定义不同登录类型的成功处理策略。
 *
 * @since 2025-06-10 23:42:54
 * @author gewuyou
 */
interface AuthenticationSuccessStrategy : AuthenticationHandlerSupportStrategy,  AuthenticationSuccessHandler