package com.gewuyou.forgeboot.security.core.common.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 *API 密钥认证令牌
 *
 * @since 2025-06-25 13:06:54
 * @author gewuyou
 */
class ApiKeyAuthenticationToken(
    val apiKey: String,
    private val principal: Any?,
    private val authorities: Collection<GrantedAuthority> = listOf()
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any = apiKey

    override fun getPrincipal(): Any? = principal

    override fun isAuthenticated(): Boolean = super.isAuthenticated
}