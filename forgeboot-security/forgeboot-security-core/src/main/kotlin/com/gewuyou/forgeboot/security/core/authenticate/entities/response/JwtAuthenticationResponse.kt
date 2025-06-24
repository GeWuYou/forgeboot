package com.gewuyou.forgeboot.security.core.authenticate.entities.response

/**
 *JWT身份验证响应
 *
 * @since 2025-06-10 15:50:10
 * @author gewuyou
 */
data class JwtAuthenticationResponse(
    override val principal: Any?,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
) : AuthenticationResponse(
    principal = principal,
    credentials = accessToken
)