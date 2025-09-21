/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 *
 */

package com.gewuyou.forgeboot.safeguard.redis.ratelimit

/**

 *
 * @since 2025-09-21 13:10:01
 * @author gewuyou
 */
import com.gewuyou.forgeboot.core.extension.withMdc
import com.gewuyou.forgeboot.safeguard.core.api.RateLimiter
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.RateLimitResult
import com.gewuyou.forgeboot.safeguard.core.policy.RateLimitPolicy
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import com.gewuyou.forgeboot.safeguard.redis.support.LuaScriptExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.data.redis.connection.ReturnType
import java.time.Instant
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

/**
 * LuaRedisRateLimiter 是一个基于 Redis 和 Lua 脚本实现的限流器。
 *
 * 该类通过执行预加载的 Lua 脚本来实现令牌桶算法，支持消费令牌和退还令牌的操作。
 *
 * @param lua 用于执行 Lua 脚本的执行器
 * @param keyBuilder 用于构建 Redis 键的工具类
 * @param context 协程上下文，默认为 Dispatchers.IO
 */
class LuaRedisRateLimiter(
    private val lua: LuaScriptExecutor,
    private val keyBuilder: RedisKeyBuilder,
    private val context: CoroutineContext = Dispatchers.IO,
) : RateLimiter {

    // 加载消费令牌的 Lua 脚本并缓存其 SHA 值
    private val sha = lua.load("lua/rate_limit_consume.lua")

    // 加载退还令牌的 Lua 脚本并缓存其 SHA 值
    private val refundSha = lua.load("lua/rate_limit_refund.lua")

    /**
     * 尝试消费一个令牌，根据限流策略判断是否允许操作。
     *
     * 该方法会调用 Lua 脚本进行令牌桶逻辑处理，返回是否允许请求以及剩余令牌数等信息。
     *
     * @param key 限流的唯一标识键
     * @param policy 限流策略，包括容量、填充速率等参数
     * @return RateLimitResult 包含是否允许、剩余令牌数和重置时间的结果对象
     */
    override fun tryConsume(key: Key, policy: RateLimitPolicy): RateLimitResult {
        return if (policy.timeout.isZero) {
            evalOnceBlocking(key, policy).toResult()
        } else {
            runBlocking(context.withMdc()) { tryConsumeAwait(key, policy) }
        }
    }

    /**
     * 异步尝试消费令牌，支持超时等待机制。
     *
     * 如果请求令牌数量超过容量，则直接失败；否则循环尝试获取令牌直到成功或超时。
     *
     * @param key 限流的唯一标识键
     * @param policy 限流策略，包括容量、填充速率等参数
     * @return RateLimitResult 包含是否允许、剩余令牌数和重置时间的结果对象
     */
    suspend fun tryConsumeAwait(key: Key, policy: RateLimitPolicy): RateLimitResult {
        val timeoutMs = policy.timeout.toMillis()
        if (timeoutMs <= 0) return evalOnceSuspend(key, policy).toResult()

        if (policy.requested > policy.capacity) {
            // 请求大于容量，不可能成功，直接失败
            return RateLimitResult(allowed = false, remaining = 0, resetAt = null)
        }

        val deadline = System.currentTimeMillis() + timeoutMs
        var last: Eval
        while (true) {
            last = evalOnceSuspend(key, policy)
            if (last.allowed) return last.toResult()

            val now = System.currentTimeMillis()
            val left = deadline - now
            if (last.waitMs <= 0 || left <= 0) {
                return last.toResult(nowInstant = Instant.now())
            }

            // 等待不超过脚本建议(waitMs)与剩余超时(left)的较小值
            val sleep = min(last.waitMs, left).coerceAtLeast(1L)
            // 避免惊群，加入少量抖动：
            val jitter = (sleep * 0.05).toLong().coerceAtLeast(1)
            delay(max(1L, sleep + (0..jitter).random()))
        }
    }

    /**
     * 封装 Lua 脚本执行结果的数据类。
     *
     * @property allowed 是否允许请求
     * @property remaining 剩余令牌数
     * @property waitMs 需要等待的毫秒数
     */
    private data class Eval(
        val allowed: Boolean,
        val remaining: Long,
        /** 距满足请求所需的等待毫秒数（由 Lua 基于 TIME 计算） */
        val waitMs: Long,
    ) {
        /**
         * 将 Eval 结果转换为 RateLimitResult。
         *
         * @param nowInstant 当前时间戳，默认为 Instant.now()
         * @return RateLimitResult 包含是否允许、剩余令牌数和重置时间的结果对象
         */
        fun toResult(nowInstant: Instant = Instant.now()): RateLimitResult {
            val resetAt = if (!allowed && waitMs > 0) nowInstant.plusMillis(waitMs) else null
            return RateLimitResult(allowed = allowed, remaining = remaining, resetAt = resetAt)
        }
    }

    /**
     * 执行一次 Lua 脚本以尝试消费令牌（阻塞方式）。
     *
     * @param key 限流的唯一标识键
     * @param policy 限流策略
     * @return Eval 封装了 Lua 脚本执行结果的对象
     */
    private fun evalOnceBlocking(key: Key, policy: RateLimitPolicy): Eval {
        val r = lua.eval(
            sha = sha,
            keys = listOf(keyBuilder.rateLimit(key)),
            returnType = ReturnType.MULTI,
            // ARGV: capacity, refill_tokens, refill_period_ms, requested
            args = listOf(
                policy.capacity,
                policy.refillTokens,
                policy.refillPeriod.toMillis(),
                policy.requested
            )
        ) as List<*>
        return Eval(
            allowed = asLong(r.getOrNull(0)) == 1L,
            remaining = asLong(r.getOrNull(1)),
            waitMs = asLong(r.getOrNull(2)),
        )
    }

    /**
     * 在协程中执行一次 Lua 脚本以尝试消费令牌。
     *
     * @param key 限流的唯一标识键
     * @param policy 限流策略
     * @return Eval 封装了 Lua 脚本执行结果的对象
     */
    private suspend fun evalOnceSuspend(key: Key, policy: RateLimitPolicy): Eval =
        withContext(context) { evalOnceBlocking(key, policy) }

    /**
     * 将任意类型转换为 Long 类型。
     *
     * @param x 待转换的值
     * @return 转换后的 Long 值
     */
    private fun asLong(x: Any?): Long = when (x) {
        null -> 0L
        is Number -> x.toLong()
        is String -> x.toLong()
        is ByteArray -> String(x, Charsets.UTF_8).toLong()
        else -> error("Unexpected type: ${x::class}")
    }

    /**
     * 退还指定数量的令牌到令牌桶中。
     *
     * 该方法会调用 Lua 脚本将一定数量的令牌返还给对应的限流键。
     *
     * @param key 限流的唯一标识键
     * @param requested 请求退还的令牌数量
     * @param policy 当前使用的限流策略
     * @return 实际退还的令牌数量
     */
    fun refund(key: Key, requested: Long, policy: RateLimitPolicy): Long {
        val res = lua.eval(
            sha = refundSha,
            returnType = ReturnType.INTEGER,
            keys = listOf(keyBuilder.rateLimit(key)),
            args = listOf(policy.capacity, requested)
        )
        return when (res) {
            is Number -> res.toLong()
            is String -> res.toLong()
            is ByteArray -> String(res, Charsets.UTF_8).toLong()
            else -> 0L
        }
    }
}
