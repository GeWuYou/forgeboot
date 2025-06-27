package com.gewuyou.forgeboot.security.authenticate.impl.controller

import com.gewuyou.forgeboot.security.authenticate.api.service.DualTokenAuthenticationService
import com.gewuyou.forgeboot.security.authenticate.api.spec.DualTokenAuthenticationControllerSpec
import com.gewuyou.forgeboot.security.core.common.constants.SecurityConstants
import com.gewuyou.forgeboot.security.core.common.entities.TokenPair
import com.gewuyou.forgeboot.webmvc.dto.R
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

/**
 *双令牌身份验证控制器
 *
 * @since 2025-06-26 20:40:12
 * @author gewuyou
 */
@RestController
class DualTokenAuthenticationController(
    private val dualTokenAuthenticationService: DualTokenAuthenticationService,
): DualTokenAuthenticationControllerSpec {
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
    @PostMapping("/auth/refresh")
    override fun refresh(
        @RequestHeader(SecurityConstants.AUTHORIZATION_HEADER)
        accessToken: String,
        @RequestHeader(SecurityConstants.REFRESH_TOKEN_HEADER)
        refreshToken: String,
    ): R<TokenPair> {
        return R.success(dualTokenAuthenticationService.refreshTokenPair(accessToken, refreshToken))
    }
}