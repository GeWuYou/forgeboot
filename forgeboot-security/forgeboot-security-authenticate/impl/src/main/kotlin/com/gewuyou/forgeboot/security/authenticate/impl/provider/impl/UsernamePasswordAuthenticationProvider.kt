package com.gewuyou.forgeboot.security.authenticate.impl.provider.impl

import com.gewuyou.forgeboot.security.authenticate.api.exception.ForgeBootAuthenticationException
import com.gewuyou.forgeboot.security.authenticate.api.service.UserCacheService
import com.gewuyou.forgeboot.security.authenticate.api.service.UserDetailsService
import com.gewuyou.forgeboot.security.authenticate.impl.constants.ForgeBootSecurityAuthenticationResponseInformation
import com.gewuyou.forgeboot.security.authenticate.impl.provider.AbstractPrincipalCredentialsAuthenticationProvider
import com.gewuyou.forgeboot.security.core.common.token.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 用户名密码身份验证提供商
 *
 * 该类用于处理基于用户名和密码的身份验证流程，继承自 AbstractPrincipalCredentialsAuthenticationProvider。
 * 提供了验证凭证、创建成功认证对象以及判断支持的认证类型的功能。
 *
 * @property userCacheService 用户缓存服务，用于获取用户缓存信息
 * @property userDetailsService 用户详情服务，用于加载用户详细信息
 * @property passwordEncoder 密码编码器，用于密码匹配校验
 *
 * @since 2025-06-11 17:12:30
 * @author gewuyou
 */
class UsernamePasswordAuthenticationProvider(
    userCacheService: UserCacheService,
    userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AbstractPrincipalCredentialsAuthenticationProvider(userCacheService, userDetailsService) {

    /**
     * 验证用户提供的凭证是否有效。
     *
     * 此方法会检查认证对象中的凭证是否为空，并使用密码编码器对原始密码和编码后的密码进行比对。
     *
     * @param user 已加载的用户详情对象
     * @param authentication 当前的认证对象，包含用户提交的凭证信息
     * @throws com.gewuyou.forgeboot.security.authenticate.api.exception.ForgeBootAuthenticationException 如果凭证不匹配或为空
     */
    override fun verifyCredentials(user: UserDetails, authentication: Authentication) {
        checkCredentialsNotNull(authentication.credentials)
        val raw = authentication.credentials.toString()
        val encoded = user.password.toString()
        if (!passwordEncoder.matches(raw, encoded)) {
            throw ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.INCORRECT_USERNAME_OR_PASSWORD)
        }
    }

    /**
     * 创建一个成功的认证对象。
     *
     * 在验证通过后，此方法将生成一个新的认证对象，表示已认证的用户状态。
     *
     * @param authentication 原始的认证请求对象
     * @param user 已验证的用户详情对象
     * @return 返回一个表示成功认证的新 Authentication 实例
     */
    override fun createSuccessAuthentication(
        authentication: Authentication,
        user: UserDetails
    ): Authentication {
        return UsernamePasswordAuthenticationToken(user, null, user.authorities)
    }

    /**
     * 判断当前提供者是否支持给定的认证类型。
     *
     * 此方法确保只处理 UsernamePasswordAuthenticationToken 类型的认证请求。
     *
     * @param authentication 认证类类型
     * @return Boolean 表示是否支持该认证类型
     */
    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}