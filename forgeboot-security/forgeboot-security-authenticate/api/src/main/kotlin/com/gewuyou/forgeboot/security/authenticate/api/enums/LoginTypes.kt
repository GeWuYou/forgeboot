package com.gewuyou.forgeboot.security.authenticate.api.enums

/**
 *登录类型
 *
 * @since 2025-06-12 21:11:02
 * @author gewuyou
 */
object LoginTypes {
    /**
     * 默认登录类型标识
     */
    const val DEFAULT = "default"
    /**
     * 用户名密码登录类型标识
     */
    const val USERNAME_PASSWORD = "username_password"

    /**
     * OAuth2 认证登录类型标识
     */
    const val OAUTH2 = "oauth2"

    /**
     * 短信验证码登录类型标识
     */
    const val SMS = "sms"
}