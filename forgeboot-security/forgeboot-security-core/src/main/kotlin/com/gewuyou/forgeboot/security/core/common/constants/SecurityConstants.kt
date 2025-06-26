package com.gewuyou.forgeboot.security.core.common.constants

/**
 * 安全相关常量定义
 *
 * 该对象存储与安全认证相关的通用常量，便于统一管理和维护。
 * 包含请求头字段名称、令牌前缀及链路标识等关键信息。
 *
 * @since 2025-06-25 16:02:05
 * @author gewuyou
 */
object SecurityConstants {
    /**
     * HTTP请求头中用于携带身份凭证的字段名称
     * 通常在请求头中使用，格式为 "Authorization: Bearer <token>"
     */
    const val AUTHORIZATION_HEADER = "Authorization"

    /**
     * HTTP请求头中用于携带刷新令牌的字段名称
     * 用于获取新的访问令牌，避免频繁登录
     */
    const val REFRESH_TOKEN_HEADER = "X-Refresh-Token"

    /**
     * Bearer Token前缀，用于在请求头中标识Token类型
     * 避免与其他类型的令牌混淆，如 Basic Auth
     */
    const val BEARER_PREFIX = "Bearer "

    /**
     * 单一令牌模式下的链路标识
     * 用于区分不同认证模式或业务场景的令牌处理逻辑
     */
    const val SINGLE_TOKEN_CHAIN_ID = "singleToken"

    /**
     * 默认的API密钥标识
     * 用于未指定具体链路时的默认认证方式
     */
    const val DEFAULT_CHAIN_ID = "default"
}