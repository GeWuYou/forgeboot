package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.config.SecurityAuthenticateProperties
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractAuthenticationFailureStrategy
import org.springframework.stereotype.Component

/**
 *默认身份验证失败策略
 *
 * @since 2025-06-14 20:26:46
 * @author gewuyou
 */
@Component("defaultAuthenticationFailureStrategy")
class DefaultAuthenticationFailureStrategy(
    objectMapper: ObjectMapper,
    properties: SecurityAuthenticateProperties
):  AbstractAuthenticationFailureStrategy(objectMapper,properties) {
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * @return 返回当前策略支持的登录类型字符串
     */
    override fun supportedLoginType(): String {
        return LoginTypes.DEFAULT
    }
}