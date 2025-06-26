package com.gewuyou.forgeboot.security.authorize.impl.core.provider

import com.gewuyou.forgeboot.security.authorize.api.core.service.SingleTokenService
import com.gewuyou.forgeboot.security.core.common.token.SingleTokenAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

/**
 * 单例 Token 认证提供者，实现 Spring Security 的 AuthenticationProvider 接口。
 *
 * 用于处理基于 SingleToken 的身份验证流程。
 *
 * @property singleTokenService 用于执行 Token 校验的服务组件。
 * @author gewuyou
 * @since 2025-06-25 13:09:43
 */
class SingleTokenAuthenticationProvider(
    private val singleTokenService: SingleTokenService
) : AuthenticationProvider {

    /**
     * 执行身份验证操作。
     *
     * 将传入的身份验证对象转换为 SingleTokenAuthenticationToken，
     * 然后通过 singleTokenService 验证 Token 的有效性，并返回认证后的 Authentication 对象。
     *
     * @param authentication 需要被验证的 Authentication 实例。
     * @return 返回已认证的 Authentication 对象。
     */
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication as SingleTokenAuthenticationToken
        val tokenInfo = singleTokenService.validate(token.singleToken)

        return SingleTokenAuthenticationToken(
            token.singleToken,
            tokenInfo.principal,
            tokenInfo.authorities
        ).apply {
            isAuthenticated = true
        }
    }

    /**
     * 判断当前 Provider 是否支持给定的身份验证类型。
     *
     * 此方法检查传入的身份验证类是否是 SingleTokenAuthenticationToken 或其子类。
     *
     * @param authentication 要检查的身份验证类。
     * @return 如果支持该类型则返回 true，否则返回 false。
     */
    override fun supports(authentication: Class<*>): Boolean {
        return SingleTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
