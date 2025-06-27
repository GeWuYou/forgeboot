package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractAuthenticationSuccessStrategy

/**
 * 默认身份验证成功策略
 *
 * 该类实现了一个基础的身份验证成功处理策略，用于处理默认类型的登录认证。
 * 继承自 [AbstractAuthenticationSuccessStrategy]，提供具体的登录类型标识。
 *
 * @property objectMapper 用于序列化/反序列化 JSON 数据的对象
 * @since 2025-06-12 22:13:42
 * @author gewuyou
 */
class DefaultAuthenticationSuccessStrategy(
    objectMapper: ObjectMapper,
) : AbstractAuthenticationSuccessStrategy(objectMapper) {
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * @return 返回当前策略支持的登录类型字符串
     */
    override fun supportedLoginTypes(): Set<String> {
        return setOf(LoginTypes.DEFAULT)
    }
}