package com.gewuyou.forgeboot.http.ktor

import com.gewuyou.forgeboot.http.ktor.entities.KtorHttpClientConfig
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * 带重试机制的执行函数
 *
 * 根据配置的重试策略，对给定的代码块进行执行，如果执行失败或返回指定的状态码，
 * 则按照配置的重试策略进行重试。
 *
 * @param conf Ktor客户端配置，包含重试相关配置
 * @param block 需要执行的代码块
 * @return 执行结果
 * @throws Exception 当达到最大重试次数或不满足重试条件时抛出异常
 */
suspend fun <T> withRetry(conf: KtorHttpClientConfig, block: suspend () -> T): T {
    // 如果未启用重试机制，则直接执行代码块并返回结果
    if (!conf.retry.enabled) return block()

    var attempt = 0
    var backoff = conf.retry.initialBackoffMillis.coerceAtLeast(1)

    while (true) {
        attempt++
        try {
            val res = block()
            // 如果结果是HttpResponse且状态码在重试列表中，并且未达到最大重试次数，则进行重试
            if (res is HttpResponse && res.status.value in conf.retry.retryOnStatus && attempt < conf.retry.maxAttempts) {
                delay(jitter(backoff, conf.retry.jitterMillis)); backoff *= 2; continue
            }
            return res
        } catch (e: Exception) {
            // 如果不满足重试条件或已达到最大重试次数，则抛出异常
            if (!conf.retry.retryOnNetworkError || attempt >= conf.retry.maxAttempts) throw e
            delay(jitter(backoff, conf.retry.jitterMillis)); backoff *= 2
        }
    }
}

/**
 * 添加抖动的延迟计算函数
 *
 * 为避免惊群效应，在基础延迟时间上添加随机抖动
 *
 * @param base 基础延迟时间（毫秒）
 * @param j 抖动范围（毫秒）
 * @return 添加抖动后的延迟时间，最小为1毫秒
 */
private fun jitter(base: Long, j: Long) = (base + Random.nextLong(-j, j + 1)).coerceAtLeast(1)