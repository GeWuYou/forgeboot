package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractLogoutSuccessStrategy

/**
 * 默认注销成功策略
 *
 * 该类实现了一个默认的注销成功处理策略，用于在用户成功注销时执行相关逻辑。
 * 继承自 [AbstractLogoutSuccessStrategy]，并使用 [objectMapper] 进行 JSON 处理。
 *
 * @property objectMapper 用于序列化/反序列化的 Jackson ObjectMapper 实例
 * @since 2025-06-14 20:38:16
 * @author gewuyou
 */
class DefaultLogoutSuccessStrategy(
    objectMapper: ObjectMapper
) : AbstractLogoutSuccessStrategy(objectMapper) {
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