package com.gewuyou.forgeboot.security.authenticate.api.service

import com.gewuyou.forgeboot.security.core.common.entities.TokenPair

/**
 * 双令牌认证服务
 *
 * 提供基于访问令牌和刷新令牌的认证机制相关功能接口，包含令牌生成与刷新逻辑。
 *
 * @since 2025-06-26 20:43:14
 * @author gewuyou
 */
interface DualTokenAuthenticationService {

    /**
     * 刷新令牌并生成新的一对令牌
     *
     * 使用旧访问令牌（可能已过期）和有效刷新令牌生成新的 TokenPair。
     * 刷新过程中会验证刷新令牌的有效性，并作废旧的令牌组合。
     *
     * @param accessToken 旧访问令牌（可能已过期）
     * @param refreshToken 有效刷新令牌
     * @return 新的 TokenPair（accessToken + refreshToken）
     */
    fun refreshTokenPair(accessToken: String, refreshToken: String): TokenPair

    /**
     * 生成新刷新令牌
     *
     * 根据系统策略生成一个安全的、唯一的刷新令牌字符串。
     * 该令牌通常具有较长的有效期且用于获取新访问令牌。
     *
     * @return 生成的刷新令牌字符串
     */
    fun generateRefreshToken(): String

    /**
     * 生成访问令牌
     *
     * 基于用户详细信息生成一个访问令牌，用于临时认证请求。
     * 该令牌通常具有较短的有效期，且不能直接用于刷新操作。
     *
     * @param userDetails 用户详细信息对象
     * @return 生成的访问令牌字符串
     */
    fun generateAccessToken(userDetails: Any): String
}