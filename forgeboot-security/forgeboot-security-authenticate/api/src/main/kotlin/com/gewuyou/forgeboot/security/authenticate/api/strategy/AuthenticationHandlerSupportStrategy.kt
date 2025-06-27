package com.gewuyou.forgeboot.security.authenticate.api.strategy

/**
 * 身份验证处理程序支持策略
 *
 * 该函数式接口定义了身份验证策略需要实现的基本规范，用于标识当前策略支持的登录类型。
 * 实现类应当通过返回特定的登录类型集合来表明该策略的应用范围。
 *
 * @since 2025-06-11 00:03:28
 * @author gewuyou
 */
fun interface AuthenticationHandlerSupportStrategy {
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * 此方法用于判断当前身份验证策略能够处理的登录请求类型。
     * 例如：可以基于不同的认证方式（如密码、短信验证码、OAuth等）进行区分。
     *
     * @return 返回当前策略支持的登录类型字符串集合
     *         集合中的每个字符串代表一种支持的登录类型标识符
     */
    fun supportedLoginTypes(): Set<String>
}