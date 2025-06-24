package com.gewuyou.forgeboot.security.core.authenticate.entities.request

/**
 *认证请求
 *
 * @since 2025-06-10 15:39:04
 * @author gewuyou
 */
open class AuthenticationRequest(
    /**
     * 认证类型
     */
    open val type: String
): LoginRequest {
    override fun getType(): String {
        return type
    }
}
