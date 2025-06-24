package com.gewuyou.forgeboot.security.authenticate.impl.constants

/**
 * Forge Boot安全身份验证响应信息
 *
 * 该对象包含用于身份验证过程中的各种预定义错误响应消息。
 * 每个属性代表一种特定的身份验证失败或系统错误情况，供在认证流程中抛出或返回给调用者。
 *
 * @since 2025-06-11 17:05:09
 * @author gewuyou
 */
object ForgeBootSecurityAuthenticationResponseInformation {
    const val INCORRECT_USERNAME_OR_PASSWORD: String = "the username or password is incorrect"

    /**
     * 当用户未提供密码时使用的响应消息。
     */
    const val PASSWORD_NOT_PROVIDED: String = "the password cannot be empty"

    /**
     * 当用户的凭证已过期时使用的响应消息。
     */
    const val ACCOUNT_CREDENTIALS_HAVE_EXPIRED: String = "the account credentials have expired"

    /**
     * 当用户账户被禁用时使用的响应消息。
     */
    const val ACCOUNT_IS_DISABLED: String = "the account is disabled"

    /**
     * 当用户账户被锁定时使用的响应消息。
     */
    const val ACCOUNT_IS_LOCKED: String = "the account is locked"

    /**
     * 当发生内部服务器错误时使用的通用错误响应消息。
     */
    const val INTERNAL_SERVER_ERROR: String = "internal server error occurred"

    /**
     * 当认证主体（principal）未提供时使用的常量字符串。
     * 这通常意味着认证请求缺少必要的身份标识信息。
     */
    const val PRINCIPAL_NOT_PROVIDED = "the authentication principal cannot be empty"

    const val LOGIN_SUCCESS: String = "login success"

    const val LOGOUT_SUCCESS: String = "logout success"
}