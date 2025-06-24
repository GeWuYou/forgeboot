package com.gewuyou.forgeboot.security.authenticate.api.exception

import org.springframework.security.core.AuthenticationException


/**
 *Forge Boot身份验证异常
 *
 * @since 2025-06-11 15:49:33
 * @author gewuyou
 */
open class ForgeBootAuthenticationException(
    msg: String,
    cause: Throwable? = null
): AuthenticationException(
    msg,
    cause
)