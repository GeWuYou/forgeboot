package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractAuthenticationFailureStrategy

/**
 *默认身份验证失败策略
 *
 * @since 2025-06-14 20:26:46
 * @author gewuyou
 */
class DefaultAuthenticationFailureStrategy(
    objectMapper: ObjectMapper,
    properties: SecurityAuthenticateProperties
):  AbstractAuthenticationFailureStrategy(objectMapper,properties) {
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * 此方法用于判断当前身份验证策略能够处理的登录请求类型。
     * 例如：可以基于不同的认证方式（如密码、短信验证码、OAuth等）进行区分。
     *
     * @return 返回当前策略支持的登录类型字符串集合
     *         集合中的每个字符串代表一种支持的登录类型标识符
     */
    override fun supportedLoginTypes(): Set<String> {
        return setOf(LoginTypes.DEFAULT)
    }
}