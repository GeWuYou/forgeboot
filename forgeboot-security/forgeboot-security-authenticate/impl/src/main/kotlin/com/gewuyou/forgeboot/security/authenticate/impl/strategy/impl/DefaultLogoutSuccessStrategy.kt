package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.impl.strategy.AbstractLogoutSuccessStrategy
import org.springframework.stereotype.Component

/**
 *默认注销成功策略
 *
 * @since 2025-06-14 20:38:16
 * @author gewuyou
 */
@Component("defaultLogoutSuccessStrategy")
class DefaultLogoutSuccessStrategy(
    objectMapper: ObjectMapper
):  AbstractLogoutSuccessStrategy(objectMapper) {
    /**
     * 获取当前策略支持的登录类型标识符。
     *
     * @return 返回当前策略支持的登录类型字符串
     */
    override fun supportedLoginType(): String {
        return LoginTypes.DEFAULT
    }

}