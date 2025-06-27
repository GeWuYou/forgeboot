package com.gewuyou.forgeboot.security.authenticate.api.spec

import com.gewuyou.forgeboot.security.core.common.entities.TokenPair
import com.gewuyou.forgeboot.webmvc.dto.R

/**
 * 双令牌身份验证控制器规格
 *
 * 定义了基于双令牌（访问令牌和刷新令牌）的身份验证机制操作规范。
 * 主要用于处理令牌刷新等核心安全认证交互流程。
 *
 * @since 2025-06-26 20:24:40
 * @author gewuyou
 */
fun interface DualTokenAuthenticationControllerSpec {
    /**
     * 刷新访问令牌
     *
     * 当访问令牌过期时，通过有效刷新令牌获取一组新的令牌对。
     * 此方法应校验刷新令牌的有效性，并生成新访问令牌。
     * 根据实现策略，刷新令牌也可能被更新并返回。
     *
     * @param accessToken 过期的访问令牌（通常通过 Authorization 头或请求体传入）
     * @param refreshToken 有效刷新令牌（用于验证并生成新令牌）
     * @return 包含新令牌对的响应对象，通常包括新访问令牌和可能更新刷新令牌
     */
    fun refresh(accessToken: String, refreshToken: String): R<TokenPair>
}