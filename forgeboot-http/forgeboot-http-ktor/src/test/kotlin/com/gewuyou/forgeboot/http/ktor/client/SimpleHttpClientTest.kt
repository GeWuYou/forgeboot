package com.gewuyou.forgeboot.http.ktor.client

import com.gewuyou.forgeboot.http.ktor.entities.KtorHttpClientConfig
import com.gewuyou.forgeboot.http.ktor.factory.HttpClientFactory
import com.gewuyou.forgeboot.http.ktor.withRetry
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class SimpleHttpClientTest {
    @Test
    fun `GET should retry on 503 and succeed on second attempt`() = runTest {
        var attempts = 0

        val engine = MockEngine { request ->
            attempts++
            if (request.url.fullPath == "/v1/ping") {
                if (attempts == 1) {
                    respondError(HttpStatusCode.ServiceUnavailable)
                } else {
                    respond(
                        content = """{"ok":true}""",
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
            } else {
                respondError(HttpStatusCode.NotFound)
            }
        }

        val conf = KtorHttpClientConfig(
            baseUrl = "https://api.example.com",
            retry = KtorHttpClientConfig.Retry(
                enabled = true, maxAttempts = 3, initialBackoffMillis = 1, jitterMillis = 0
            )
        )

        val client = HttpClientFactory.create(engine, conf)
        val http = SimpleHttpClient(client, conf)

        val res: Map<String, Boolean> = http.get("/v1/ping")

        assertTrue(res["ok"] == true, "should parse JSON body to Map and get ok=true")
        assertEquals(2, attempts, "should retry exactly once (503 -> 200)")
    }

    @Test
    fun `POST should send JSON and parse response`() = runTest {
        val engine = MockEngine { request ->
            val bodyCt = request.body.contentType
            assertEquals(ContentType.Application.Json, bodyCt?.withoutParameters())
            assertEquals("https://api.example.com/v1/echo", request.url.toString())

            respond(
                content = """{"echo":true}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val conf = KtorHttpClientConfig(baseUrl = "https://api.example.com")
        val client = HttpClientFactory.create(engine, conf)

        val res: Map<String, Boolean> = withRetry(conf) {
            client.post("/v1/echo") {
                contentType(ContentType.Application.Json)
                setBody("""{"x":1}""")
            }
        }.body()

        assertTrue(res["echo"] == true)
    }


}