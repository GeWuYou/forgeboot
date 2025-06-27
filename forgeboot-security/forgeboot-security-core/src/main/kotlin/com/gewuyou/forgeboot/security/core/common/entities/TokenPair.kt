package com.gewuyou.forgeboot.security.core.common.entities

/**
 * 令牌对
 *
 * 该类用于封装访问令牌和刷新令牌及其有效期
 *
 * @property accessToken 访问令牌，用于身份验证
 * @property refreshToken 刷新令牌，用于获取新访问令牌
 * @property expiresIn 访问令牌的有效期，单位为秒
 *
 * @since 2025-06-16 13:28:33
 * @author gewuyou
 */
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)