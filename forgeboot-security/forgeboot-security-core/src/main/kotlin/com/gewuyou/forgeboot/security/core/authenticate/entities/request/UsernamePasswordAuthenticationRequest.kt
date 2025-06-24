package com.gewuyou.forgeboot.security.core.authenticate.entities.request

/**
 * 用户名密码认证请求类
 *
 * 该类用于封装基于用户名和密码的认证请求数据，继承自[AuthenticationRequest]。
 * 提供了用户名和密码字段，并设置默认认证类型为"password"。
 *
 * @property type 认证类型，默认值为"password"
 * @property username 用户输入的用户名
 * @property password 用户输入的密码
 *
 * @author gewuyou
 * @since 2025-06-10 15:40:17
 */
data class UsernamePasswordAuthenticationRequest(
    override val type: String = "username",
    val username: String,
    val password: String
) : AuthenticationRequest(type)
