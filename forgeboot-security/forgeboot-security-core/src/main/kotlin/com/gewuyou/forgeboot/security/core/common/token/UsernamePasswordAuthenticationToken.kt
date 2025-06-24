package com.gewuyou.forgeboot.security.core.common.token

import org.springframework.security.core.GrantedAuthority

/**
 *用户名 密码 认证 Token
 *
 * @since 2025-02-16 16:00:18
 * @author gewuyou
 */
class UsernamePasswordAuthenticationToken(
    principal: Any,
    credentials: Any?,
    authorities: Collection<GrantedAuthority>? = null
) : PrincipalCredentialsAuthenticationToken(principal, credentials, authorities) {
    companion object {
        /**
         * 创建未认证的令牌
         */
        @JvmStatic
        fun unauthenticated(principal: Any, credentials: Any): UsernamePasswordAuthenticationToken {
            return UsernamePasswordAuthenticationToken(principal, credentials)
        }

        /**
         * 创建已认证的令牌
         */
        @JvmStatic
        fun authenticated(
            principal: Any,
            credentials: Any?,
            authorities: Collection<GrantedAuthority>
        ): UsernamePasswordAuthenticationToken {
            return UsernamePasswordAuthenticationToken(principal, credentials, authorities)
        }
    }
}