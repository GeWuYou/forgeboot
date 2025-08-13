package com.gewuyou.forgeboot.http.ktor.client

import com.gewuyou.forgeboot.http.ktor.entities.KtorHttpClientConfig
import com.gewuyou.forgeboot.http.ktor.withRetry
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * 简单的HTTP客户端
 *
 * 提供了基本的HTTP操作方法，包括GET、POST、PUT和DELETE，并内置重试机制。
 * 所有请求都会根据配置的重试策略在失败时自动重试。
 *
 * @property client Ktor HTTP客户端实例
 * @property conf Ktor客户端配置，包含重试等相关配置
 * @since 2025-08-13 15:14:50
 * @author gewuyou
 */
class SimpleHttpClient(
    val client: HttpClient,
    val conf: KtorHttpClientConfig,
) {
    /**
     * 执行GET请求
     *
     * @param path 请求路径
     * @return 响应结果，类型为R
     */
    suspend inline fun <reified R> get(path: String): R =
        withRetry(conf) { client.get(path) }.body()

    /**
     * 执行POST请求
     *
     * @param path 请求路径
     * @param body 请求体数据
     * @return 响应结果，类型为Res
     */
    suspend inline fun <reified Req : Any, reified Res> post(path: String, body: Req): Res =
        withRetry(conf) { client.post(path) { contentType(ContentType.Application.Json); setBody(body) } }.body()

    /**
     * 执行PUT请求
     *
     * @param path 请求路径
     * @param body 请求体数据
     * @return 响应结果，类型为Res
     */
    suspend inline fun <reified Req : Any, reified Res> put(path: String, body: Req): Res =
        withRetry(conf) { client.put(path) { contentType(ContentType.Application.Json); setBody(body) } }.body()

    /**
     * 执行DELETE请求
     *
     * @param path 请求路径
     * @return 响应结果，类型为R
     */
    suspend inline fun <reified R> delete(path: String): R =
        withRetry(conf) { client.delete(path) }.body()

    /**
     * 关闭HTTP客户端，释放资源
     */
    fun close() = client.close()
}