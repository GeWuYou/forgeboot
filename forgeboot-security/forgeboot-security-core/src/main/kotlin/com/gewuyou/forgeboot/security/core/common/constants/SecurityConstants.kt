package com.gewuyou.forgeboot.security.core.common.constants

/**
 * 安全相关常量定义
 *
 * 该对象存储与安全认证相关的通用常量，便于统一管理和维护。
 *
 * @since 2025-06-25 16:02:05
 * @author gewuyou
 */
object SecurityConstants {
    /**
     * HTTP请求头中用于携带身份凭证的字段名称
     */
    const val AUTHORIZATION_HEADER = "Authorization"

    /**
     * HTTP请求头中用于携带刷新令牌的字段名称
     */
    const val REFRESH_TOKEN_HEADER="X-Refresh-Token"

    /**
     * Bearer Token前缀，用于在请求头中标识Token类型
     */
    const val BEARER_PREFIX = "Bearer "

    /**
     * API密钥请求头字段名称，用于在请求头中携带API认证标识
     */
    const val API_KEY_CHAIN_ID = "apiKey"

    /**
     * 默认的API密钥标识
     */
    const val DEFAULT_CHAIN_ID = "default"
}