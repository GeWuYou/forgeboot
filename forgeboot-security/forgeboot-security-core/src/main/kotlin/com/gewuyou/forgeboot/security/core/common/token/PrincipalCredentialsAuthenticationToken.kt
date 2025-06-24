package com.gewuyou.forgeboot.security.core.common.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.util.Assert

/**
 * 主体凭据身份验证令牌
 *
 * @author gewuyou
 * @since 2025-02-16 15:46:05
 */
open class PrincipalCredentialsAuthenticationToken(
    /**
     * 主体
     */
    @Transient
    private val principal: Any,
    /**
     * 凭证
     */
    @Transient
    private val credentials: Any?,
    /**
     * 权限列表
     */
    authorities: Collection<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    init {
        if (authorities == null) {
            isAuthenticated = false
        } else {
            super.setAuthenticated(true)
        }
    }
    companion object {
        /**
         * 创建未认证的令牌
         */
        @JvmStatic
        fun unauthenticated(principal: Any, credentials: Any): PrincipalCredentialsAuthenticationToken {
            return PrincipalCredentialsAuthenticationToken(principal, credentials)
        }

        /**
         * 创建已认证的令牌
         */
        @JvmStatic
        fun authenticated(
            principal: Any,
            credentials: Any?,
            authorities: Collection<GrantedAuthority>
        ): PrincipalCredentialsAuthenticationToken {
            return PrincipalCredentialsAuthenticationToken(principal, credentials, authorities)
        }
    }

    /**
     * The credentials that prove the principal is correct. This is usually a password,
     * but could be anything relevant to the `AuthenticationManager`. Callers
     * are expected to populate the credentials.
     *
     * @return the credentials that prove the identity of the `Principal`
     */
    override fun getCredentials(): Any? {
        return if (isAuthenticated) null else this.credentials
    }

    /**
     * The identity of the principal being authenticated. In the case of an authentication
     * request with username and password, this would be the username. Callers are
     * expected to populate the principal for an authentication request.
     *
     *
     * The <tt>AuthenticationManager</tt> implementation will often return an
     * <tt>Authentication</tt> containing richer information as the principal for use by
     * the application. Many of the authentication providers will create a
     * `UserDetails` object as the principal.
     *
     * @return the `Principal` being authenticated or the authenticated
     * principal after authentication.
     */
    override fun getPrincipal(): Any {
        return this.principal
    }

    /**
     * 重写方法防止滥用
     * @param authenticated `true` if the token should be trusted (which may
     * result in an exception) or `false` if the token should not be trusted
     */
    override fun setAuthenticated(authenticated: Boolean) {
        Assert.isTrue(
            !isAuthenticated,
            "无法将此令牌设置为受信任 - 请使用采用 GrantedAuthority 列表的构造函数"
        )
        super.setAuthenticated(false)
    }
}

