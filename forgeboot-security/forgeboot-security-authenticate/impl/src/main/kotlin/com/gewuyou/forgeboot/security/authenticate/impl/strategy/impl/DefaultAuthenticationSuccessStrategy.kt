package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractAuthenticationSuccessStrategy
import org.springframework.stereotype.Component

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
@Component("defaultAuthenticationSuccessStrategy")
class DefaultAuthenticationSuccessStrategy(
    objectMapper: ObjectMapper,
) : AbstractAuthenticationSuccessStrategy(objectMapper) {
    /**
     * 获取当前策略支持的登录类型标识
     *
     * 此方法返回 "default" 字符串，表示该策略适用于默认登录类型。
     * 在多策略环境下，通过此标识来匹配相应的处理逻辑。
     *
     * @return 返回支持的登录类型标识字符串
     */
    override fun supportedLoginType(): String = LoginTypes.DEFAULT
}