package com.gewuyou.forgeboot.http.ktor.factory

import com.gewuyou.forgeboot.http.ktor.entities.KtorHttpClientConfig
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


/**
 * HTTP客户工厂
 *
 * 用于创建和配置Ktor HTTP客户端的工厂对象。提供了多种创建方式，
 * 支持使用已实例化的引擎或引擎工厂来创建客户端，并统一配置插件和默认请求设置。
 *
 * @since 2025-08-13 14:56:45
 * @author gewuyou
 */
object HttpClientFactory {

    /**
     * 使用已实例化的引擎创建HTTP客户端
     *
     * 由调用方完全掌控引擎的配置与生命周期
     *
     * @param engine 已实例化的HTTP客户端引擎
     * @param conf Ktor客户端配置
     * @return 配置好的HttpClient实例
     */
    fun create(engine: HttpClientEngine, conf: KtorHttpClientConfig): HttpClient =
        build(HttpClient(engine), conf)

    /**
     * 使用引擎工厂创建HTTP客户端
     *
     * 由Ktor负责创建和管理引擎，适用于简单场景
     *
     * @param engineFactory HTTP客户端引擎工厂
     * @param conf Ktor客户端配置
     * @param configureEngine 引擎配置函数，可选
     * @return 配置好的HttpClient实例
     */
    fun <TConfig : HttpClientEngineConfig> create(
        engineFactory: HttpClientEngineFactory<TConfig>,
        conf: KtorHttpClientConfig,
        configureEngine: (TConfig.() -> Unit)? = null,
    ): HttpClient = build(HttpClient(engineFactory) {
        configureEngine?.let { engine(it) }
    }, conf)

    /**
     * 统一安装插件和配置默认请求
     *
     * 配置内容包括内容协商、日志记录、认证头、默认内容类型和基础URL等
     *
     * @param client HTTP客户端实例
     * @param conf Ktor客户端配置
     * @return 配置好的HttpClient实例
     */
    private fun build(client: HttpClient, conf: KtorHttpClientConfig): HttpClient = client.config {
        // 安装内容协商插件并配置JSON序列化
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = conf.json.ignoreUnknownKeys
                    prettyPrint = conf.json.prettyPrint
                    explicitNulls = conf.json.explicitNulls
                }
            )
        }

        // 根据配置决定是否安装日志插件
        if (conf.logging.enabled) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = when (conf.logging.level.uppercase()) {
                    "NONE" -> LogLevel.NONE
                    "HEADERS" -> LogLevel.HEADERS
                    "BODY" -> LogLevel.BODY
                    else -> LogLevel.INFO
                }
            }
        }

        // 配置默认请求设置
        defaultRequest {
            // 根据认证配置添加相应的认证头
            when (val a = conf.auth) {
                is KtorHttpClientConfig.Auth.ApiKey -> headers.append(a.header, a.value)
                is KtorHttpClientConfig.Auth.Bearer -> headers.append("Authorization", "Bearer ${a.token}")
                is KtorHttpClientConfig.Auth.Basic -> {
                    val raw = "${a.username}:${a.password}"
                    val basic = java.util.Base64.getEncoder().encodeToString(raw.toByteArray())
                    headers.append("Authorization", "Basic $basic")
                }

                KtorHttpClientConfig.Auth.None -> {
                    // not required
                }
            }
            // 设置默认内容类型为JSON
            if (contentType() == null) contentType(ContentType.Application.Json)
            // 设置基础URL（当使用相对路径时生效）
            conf.baseUrl?.let { url(it) }
        }
    }
}