package com.gewuyou.forgeboot.security.core.common.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * 用于单令牌认证的认证令牌实现类。该类扩展了 Spring Security 的 AbstractAuthenticationToken，
 * 用于表示基于单一令牌（如 API Key）的认证请求。
 *
 * @param singleToken 存储认证凭据（如API密钥或令牌），不可为 null。
 * @param principal 表示经过认证的主体，可以是用户对象或其他形式的身份标识。
 * @param authorities 用户所拥有的权限集合，默认为空列表。
 *
 * @author gewuyou
 * @since 2025-06-25 13:06:54
 */
class SingleTokenAuthenticationToken(
    val singleToken: String,
    private val principal: Any?,
    authorities: Collection<GrantedAuthority> = listOf()
) : AbstractAuthenticationToken(authorities) {

    /**
     * 获取认证凭据。
     *
     * @return 返回存储在 [singleToken] 中的认证凭据。
     */
    override fun getCredentials(): Any = singleToken

    /**
     * 获取认证主体。
     *
     * @return 返回认证主体对象，可能为 null。
     */
    override fun getPrincipal(): Any? = principal

    /**
     * 判断当前认证是否已完成。
     *
     * @return 如果认证成功则返回 true；否则返回 false。
     */
    override fun isAuthenticated(): Boolean = super.isAuthenticated
}