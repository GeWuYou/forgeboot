package com.gewuyou.forgeboot.security.core.authenticate.entities.request

/**
 *登录请求
 *
 * @since 2025-02-15 02:13:16
 * @author gewuyou
 */
fun interface LoginRequest {
    fun getType(): String
}