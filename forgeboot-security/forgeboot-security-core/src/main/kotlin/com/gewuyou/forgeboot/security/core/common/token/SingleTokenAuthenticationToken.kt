package com.gewuyou.forgeboot.security.core.common.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * 用于单令牌认证的认证令牌实现类。该类扩展了 Spring Security 的 AbstractAuthenticationToken，
 * 用于表示基于单一令牌（如 API Key）的认证请求。
 *
 * @param principal 表示经过认证的主体，可以是用户对象或其他形式的身份标识，不可为 null。
 * @param authorities 用户所拥有的权限集合，默认为空列表。
 *
 * @author gewuyou
 * @since 2025-06-25 13:06:54
 */
class SingleTokenAuthenticationToken(
    private val principal: Any,
    authorities: Collection<GrantedAuthority>? = null,
) : AbstractAuthenticationToken(authorities) {

    /**
     * 初始化方法，根据是否提供权限信息设置认证状态。
     * 如果权限信息为 null，则认证状态设为未认证；否则设为已认证。
     */
    init {
        if (authorities == null) {
            super.setAuthenticated(false)
        } else {
            super.setAuthenticated(true)
        }
    }

    companion object {
        /**
         * 创建一个未认证的 SingleTokenAuthenticationToken 实例。
         *
         * @param token 认证凭据（如 API 密钥或令牌），不可为 null。
         * @return 返回未认证的 SingleTokenAuthenticationToken 实例。
         */
        @JvmStatic
        fun unauthenticated(token: String): SingleTokenAuthenticationToken {
            return SingleTokenAuthenticationToken(token)
        }

        /**
         * 创建一个已认证的 SingleTokenAuthenticationToken 实例。
         *
         * @param principal 表示经过认证的主体，不可为 null。
         * @param authorities 用户所拥有的权限集合，不可为 null。
         * @return 返回已认证的 SingleTokenAuthenticationToken 实例。
         */
        @JvmStatic
        fun authenticated(
            principal: Any,
            authorities: Collection<GrantedAuthority>,
        ): SingleTokenAuthenticationToken {
            return SingleTokenAuthenticationToken(principal.toString(), authorities)
        }
    }

    /**
     * 获取认证凭据。对于单令牌认证来说，凭证通常不适用，因此返回 null。
     *
     * @return 始终返回 null。
     */
    override fun getCredentials(): Any? = null

    /**
     * 获取认证的主体信息。
     *
     * @return 返回认证的主体对象。
     */
    override fun getPrincipal(): Any = principal

    /**
     * 禁止直接设置认证状态。应使用工厂方法创建已认证或未认证的实例。
     *
     * @param authenticated 认证状态，此参数将被忽略。
     * @throws IllegalArgumentException 总是抛出此异常以防止直接修改认证状态。
     */
    override fun setAuthenticated(authenticated: Boolean) {
        throw IllegalArgumentException("请使用 factory 方法设置认证状态")
    }
}