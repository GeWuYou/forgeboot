package com.gewuyou.forgeboot.security.authenticate.impl.provider


import com.gewuyou.forgeboot.security.authenticate.api.exception.ForgeBootAuthenticationException
import com.gewuyou.forgeboot.security.authenticate.api.service.UserCacheService
import com.gewuyou.forgeboot.security.authenticate.api.service.UserDetailsService
import com.gewuyou.forgeboot.security.authenticate.impl.constants.ForgeBootSecurityAuthenticationResponseInformation
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException

import org.springframework.security.core.userdetails.UserDetails
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 通用主体凭据认证提供器（抽象类）
 * 负责封装认证流程中通用的逻辑，凭据校验和构建成功认证对象由子类完成。
 *
 * @author gewuyou
 * @since 2025-06-11
 */
abstract class AbstractPrincipalCredentialsAuthenticationProvider(
    private val userCacheService: UserCacheService,
    private val userDetailsService: UserDetailsService
) : AuthenticationProvider {

    /**
     * 执行认证的核心方法
     *
     * @param authentication 待认证的 Authentication 对象
     * @return 成功认证后的 Authentication 对象
     * @throws ForgeBootAuthenticationException 认证失败时抛出异常
     */
    override fun authenticate(authentication: Authentication): Authentication {
        val principal = getPrincipal(authentication)
        val fromCache = AtomicBoolean(true)

        // Step 1: 获取用户（缓存优先）
        var user = userCacheService.getUserFromCache(principal) ?: run {
            fromCache.set(false)
            retrieveUser(principal)
        }

        try {
            preAuthenticationCheck(user)
            verifyCredentials(user, authentication)
            postAuthenticationCheck(user)
        } catch (e: AuthenticationException) {
            if (fromCache.get()) {
                // 如果缓存失败则尝试数据库
                user = retrieveUser(principal)
                preAuthenticationCheck(user)
                verifyCredentials(user, authentication)
                postAuthenticationCheck(user)
            } else {
                throw e
            }
        }

        if (!fromCache.get()) {
            userCacheService.putUserToCache(user)
        }

        return createSuccessAuthentication(authentication, user)
    }

    /**
     * 判断该 Provider 是否支持指定类型的认证对象
     *
     * @param authentication 需要判断的认证类型
     * @return Boolean 表示是否支持该类型认证
     */
    abstract override fun supports(authentication: Class<*>): Boolean

    /**
     * 从数据源获取用户信息
     *
     * @param principal 用户标识
     * @return UserDetails 用户详细信息
     * @throws ForgeBootAuthenticationException 当 principal 为空或加载失败时抛出异常
     */
    protected open fun retrieveUser(principal: String): UserDetails {
        if (principal.isBlank()) {
            ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.PRINCIPAL_NOT_PROVIDED)
        }
        return try {
            userDetailsService.loadUserByPrincipal(principal)
        } catch (e: Exception) {
            throw ForgeBootAuthenticationException(
                ForgeBootSecurityAuthenticationResponseInformation.INTERNAL_SERVER_ERROR,
                e
            )
        }
    }

    /**
     * 在验证凭据前进行账户状态检查
     *
     * @param user 用户详情对象
     * @throws ForgeBootAuthenticationException 当账户锁定或禁用时抛出异常
     */
    protected open fun preAuthenticationCheck(user: UserDetails) {
        if (!user.isAccountNonLocked) {
            throw ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.ACCOUNT_IS_LOCKED)
        }
        if (!user.isEnabled) {
            throw ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.ACCOUNT_IS_DISABLED)
        }
    }

    /**
     * 在验证凭据后进行凭证有效期检查
     *
     * @param user 用户详情对象
     * @throws ForgeBootAuthenticationException 当凭证已过期时抛出异常
     */
    protected open fun postAuthenticationCheck(user: UserDetails) {
        if (!user.isCredentialsNonExpired) {
            throw ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.ACCOUNT_CREDENTIALS_HAVE_EXPIRED)
        }
    }

    /**
     * 检查凭证是否为 null
     *
     * @param credentials 待检查的凭证对象
     * @throws ForgeBootAuthenticationException 当凭证为 null 时抛出异常
     */
    protected fun checkCredentialsNotNull(credentials: Any?) {
        credentials ?: ForgeBootAuthenticationException(ForgeBootSecurityAuthenticationResponseInformation.PASSWORD_NOT_PROVIDED)
    }

    /**
     * 提取认证对象中的用户标识
     *
     * @param authentication Authentication 对象
     * @return String 用户标识字符串
     */
    protected fun getPrincipal(authentication: Authentication): String =
        authentication.principal.toString()

    /**
     * 验证用户凭据的具体实现，由子类完成
     *
     * @param user 用户详情对象
     * @param authentication 待验证的 Authentication 对象
     * @throws ForgeBootAuthenticationException 凭据错误时抛出异常
     */
    protected abstract fun verifyCredentials(user: UserDetails, authentication: Authentication)

    /**
     * 构建成功认证后的 Authentication 对象，由子类实现
     *
     * @param authentication 原始的 Authentication 对象
     * @param user 用户详情对象
     * @return 成功认证后的 Authentication 对象
     */
    protected abstract fun createSuccessAuthentication(
        authentication: Authentication,
        user: UserDetails
    ): Authentication
}
