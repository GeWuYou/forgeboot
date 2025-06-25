package com.gewuyou.forgeboot.security.authorize.api.core.service

import com.gewuyou.forgeboot.security.core.authorize.entities.ApiKeyPrincipal

/**
 *API 密钥服务
 *
 * @since 2025-06-25 13:10:38
 * @author gewuyou
 */
fun interface ApiKeyService {
    fun validate(apiKey: String): ApiKeyPrincipal  // 负责验证与解析
}