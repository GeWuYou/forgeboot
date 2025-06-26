package com.gewuyou.forgeboot.security.authorize.impl.core.provider

import com.gewuyou.forgeboot.security.authorize.api.core.validator.SingleTokenValidator
import com.gewuyou.forgeboot.security.core.authorize.entities.SingleTokenPrincipal
import com.gewuyou.forgeboot.security.core.common.token.SingleTokenAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

/**
 * 单例 Token 认证提供者，实现 Spring Security 的 AuthenticationProvider 接口。
 *
 * 用于处理基于 SingleToken 的身份验证流程。
 *
 * @property singleTokenValidator 用于执行 Token 校验的服务组件。
 * @author gewuyou
 * @since 2025-06-25 13:09:43
 */
class SingleTokenAuthenticationProvider(
    private val singleTokenValidator: SingleTokenValidator<SingleTokenPrincipal>
) : AuthenticationProvider {

    /**
     * 执行身份验证操作。
     *
     * 将传入的身份验证对象转换为 SingleTokenAuthenticationToken 类型，
     * 然后通过 singleTokenService 验证 Token 的有效性，并返回认证后的 Authentication 对象。
     *
     * 该方法的主要流程包括：
     * 1. 强制类型转换输入的 Authentication 对象为 SingleTokenAuthenticationToken；
     * 2. 使用 Token 的 principal 值调用 singleTokenService 进行 Token 校验；
     * 3. 构建并返回已认证的 Authentication 实例。
     *
     * @param authentication 需要被验证的 Authentication 实例，必须是 SingleTokenAuthenticationToken 类型。
     * @return 返回一个已认证的 Authentication 对象，表示身份验证成功的结果。
     */
    override fun authenticate(authentication: Authentication): Authentication {
        // 转换 Authentication 为 SingleTokenAuthenticationToken 类型
        val token = authentication as SingleTokenAuthenticationToken

        // 使用 Token 的 principal 值进行校验，获取 Token 信息
        val tokenInfo = singleTokenValidator.validate(token.principal.toString())

        // 创建已认证的 Authentication 实例并返回
        return SingleTokenAuthenticationToken.authenticated(tokenInfo, tokenInfo.authorities)
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
        // 检查传入的身份验证类是否是 SingleTokenAuthenticationToken 或其子类
        return SingleTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
