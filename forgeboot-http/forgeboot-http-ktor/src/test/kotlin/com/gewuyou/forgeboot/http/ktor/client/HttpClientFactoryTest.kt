package com.gewuyou.forgeboot.http.ktor.client

import com.gewuyou.forgeboot.http.ktor.entities.KtorHttpClientConfig
import com.gewuyou.forgeboot.http.ktor.factory.HttpClientFactory
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *HttpClientFactoryTest
 *
 * @since 2025-08-13 15:45:50
 * @author gewuyou
 */
class HttpClientFactoryTest {
    @Test
    fun `factory should apply baseUrl, auth header and default content-type`() = runTest {
        var capturedUrl: String? = null
        var capturedAuth: String? = null
        var capturedContentType: String? = null

        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            capturedAuth = request.headers[HttpHeaders.Authorization]
            capturedContentType = request.headers[HttpHeaders.ContentType] // 来自 defaultRequest 的默认 JSON
            respond(
                content = """{"ok":true}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val conf = KtorHttpClientConfig(
            baseUrl = "https://example.com",
            auth = KtorHttpClientConfig.Auth.Bearer("TOKEN-XYZ"),
            // 打开日志不会影响请求，但要保证装配不报错
            logging = KtorHttpClientConfig.Logging(enabled = true, level = "INFO")
        )

        val client: HttpClient = HttpClientFactory.create(engine, conf)

        // 不通过 SimpleHttpClient，直接用 HttpClient 也应该生效
        client.get("/echo")

        assertEquals("https://example.com/echo", capturedUrl)
        assertEquals("Bearer TOKEN-XYZ", capturedAuth)
        assertEquals(ContentType.Application.Json.toString(), capturedContentType)
    }

    @Test
    fun `factory create with engineFactory should accept engine configure lambda`() = runTest {
        // 这里用 MockEngineFactory + MockEngineConfig 验证“引擎工厂”重载能正常工作
        val engineFactory = MockEngine
        var seen = false

        val conf = KtorHttpClientConfig(baseUrl = "https://e.com")

        val client = HttpClientFactory.create(engineFactory, conf) {
            seen = true
            addHandler { request ->
                // 命中即证明客户端按 baseUrl 拼接了路径
                if (request.url.toString() == "https://e.com/ping") {
                    respond(
                        content = """{"ok":true}""",
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                } else {
                    respond(
                        content = """{"ok":false}""",
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            }
        }

        // 发起请求，触发我们在 configureEngine 中注册的 handler
        client.get("/ping")

        assertTrue(seen, "engine configure lambda should be executed")
    }
}