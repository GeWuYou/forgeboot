package com.gewuyou.forgeboot.http.ktor.entities

import java.time.Duration

/**
 * HTTP客户端配置
 *
 * 用于配置HTTP客户端的各种参数，包括基础URL、超时设置、连接数限制、认证信息、
 * JSON处理配置、日志配置和重试策略等。
 *
 * @property baseUrl 基础URL地址，所有请求将基于此URL进行构建
 * @property connectTimeout 连接超时时间，默认5秒
 * @property requestTimeout 请求超时时间，默认30秒
 * @property socketTimeout Socket超时时间，默认30秒
 * @property maxConnections 最大连接数，默认100
 * @property auth 认证配置，支持无认证、API Key、Bearer Token和Basic认证
 * @property json JSON序列化配置
 * @property logging 日志配置
 * @property retry 重试策略配置
 * @since 2025-08-13 14:54:44
 * @author gewuyou
 */
data class KtorHttpClientConfig(
    val baseUrl: String? = null,
    val connectTimeout: Duration = Duration.ofSeconds(5),
    val requestTimeout: Duration = Duration.ofSeconds(30),
    val socketTimeout: Duration = Duration.ofSeconds(30),
    val maxConnections: Int = 100,
    val auth: Auth = Auth.None,
    val json: Json = Json(),
    val logging: Logging = Logging(),
    val retry: Retry = Retry(),
) {
    /**
     * 认证接口，定义了不同类型的认证方式
     */
    sealed interface Auth {
        /**
         * 无认证
         */
        data object None : Auth

        /**
         * API Key认证
         * @property header 认证头名称
         * @property value 认证值
         */
        data class ApiKey(val header: String, val value: String) : Auth

        /**
         * Bearer Token认证
         * @property token 认证令牌
         */
        data class Bearer(val token: String) : Auth

        /**
         * Basic认证
         * @property username 用户名
         * @property password 密码
         */
        data class Basic(val username: String, val password: String) : Auth
    }

    /**
     * JSON配置
     *
     * @property ignoreUnknownKeys 是否忽略未知的JSON字段，默认为true
     * @property prettyPrint 是否格式化输出JSON，默认为false
     * @property explicitNulls 是否显式输出null值，默认为false
     */
    data class Json(
        val ignoreUnknownKeys: Boolean = true,
        val prettyPrint: Boolean = false,
        val explicitNulls: Boolean = false,
    )

    /**
     * 日志配置
     *
     * @property enabled 是否启用日志，默认为true
     * @property level 日志级别，默认为"INFO"
     */
    data class Logging(val enabled: Boolean = true, val level: String = "INFO")

    /**
     * 重试策略配置
     *
     * @property enabled 是否启用重试机制，默认为false
     * @property maxAttempts 最大重试次数，默认为3次
     * @property initialBackoffMillis 初始退避时间（毫秒），默认为200毫秒
     * @property jitterMillis 抖动时间（毫秒），默认为50毫秒
     * @property retryOnStatus 需要重试的HTTP状态码集合，默认包含429, 500, 502, 503, 504
     * @property retryOnNetworkError 是否在网络错误时重试，默认为true
     */
    data class Retry(
        val enabled: Boolean = false,
        val maxAttempts: Int = 3,
        val initialBackoffMillis: Long = 200,
        val jitterMillis: Long = 50,
        val retryOnStatus: Set<Int> = setOf(429, 500, 502, 503, 504),
        val retryOnNetworkError: Boolean = true,
    )
}