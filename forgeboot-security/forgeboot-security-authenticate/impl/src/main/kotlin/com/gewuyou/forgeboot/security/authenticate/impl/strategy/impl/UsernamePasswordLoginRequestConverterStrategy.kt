package com.gewuyou.forgeboot.security.authenticate.impl.strategy.impl

import com.gewuyou.forgeboot.security.authenticate.api.enums.LoginTypes
import com.gewuyou.forgeboot.security.authenticate.api.strategy.LoginRequestConverterStrategy
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.LoginRequest
import com.gewuyou.forgeboot.security.core.authenticate.entities.request.UsernamePasswordAuthenticationRequest
import com.gewuyou.forgeboot.security.core.common.token.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * 用户名 密码 认证 Token 转换器策略
 * @since 2025-02-15 03:25:14
 * @author gewuyou
 */
@Component("usernamePasswordLoginRequestConverterStrategy")
class UsernamePasswordLoginRequestConverterStrategy : LoginRequestConverterStrategy {
    /**
     * 转换登录请求为认证对象
     * @param loginRequest 登录请求
     * @return 认证对象
     */
    override fun convert(loginRequest: LoginRequest): Authentication {
        if (loginRequest is UsernamePasswordAuthenticationRequest) {
            return UsernamePasswordAuthenticationToken.Companion.unauthenticated(loginRequest.username, loginRequest.password)
        }
        throw IllegalArgumentException("Unsupported login request type: ${loginRequest.javaClass.name}")
    }

    /**
     * 获取支持的登录类型
     * @return 请求的登录类型
     */
    override fun getSupportedLoginType(): String {
        return LoginTypes.USERNAME_PASSWORD
    }
}