package com.gewuyou.forgeboot.security.authenticate.api.strategy

/**
 *身份验证处理程序支持策略
 *
 * @since 2025-06-11 00:03:28
 * @author gewuyou
 */
fun interface AuthenticationHandlerSupportStrategy{
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * @return 返回当前策略支持的登录类型字符串
     */
    fun supportedLoginType(): String
}