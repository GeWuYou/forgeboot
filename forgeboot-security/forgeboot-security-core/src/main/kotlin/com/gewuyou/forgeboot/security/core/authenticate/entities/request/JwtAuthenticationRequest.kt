package com.gewuyou.forgeboot.security.core.authenticate.entities.request

/**
 * JWT身份验证请求数据类
 *
 * 该类用于封装基于JWT的身份验证请求信息，继承自[AuthenticationRequest]。
 * 主要包含身份验证类型和JWT令牌两个属性，适用于系统中的身份认证流程。
 *
 * @property type 身份验证的类型，覆盖父类属性，默认值为"jwt"
 * @property token JWT访问令牌，用于身份验证和授权
 *
 * @since 2025-06-10 15:41:22
 * @author gewuyou
 */
data class JwtAuthenticationRequest(
    override val type: String = "jwt",
    val token: String
) : AuthenticationRequest(type)