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
package com.gewuyou.forgeboot.safeguard.redis.attemptlimit

import com.gewuyou.forgeboot.safeguard.core.api.AttemptLimitManager
import com.gewuyou.forgeboot.safeguard.core.extensions.toCsv
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.AttemptCheck
import com.gewuyou.forgeboot.safeguard.core.model.AttemptLimitResult
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import com.gewuyou.forgeboot.safeguard.core.util.ConversionUtils
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import com.gewuyou.forgeboot.safeguard.redis.support.LuaScriptExecutor
/**
 * 基于 Redis + Lua 的尝试限制管理器。
 *
 * 该类通过 Lua 脚本实现对尝试次数的限制控制，支持窗口限流、锁定机制和失败升级策略。
 * 使用 Redis 存储尝试次数、锁定状态和失败计数等信息。
 *
 * @property lua Lua 脚本执行器，用于加载和执行 Lua 脚本。
 * @property keyBuilder Redis 键构建器，用于生成 Redis 中使用的键。
 */
class LuaAttemptLimitManager(
    private val lua: LuaScriptExecutor,
    private val keyBuilder: RedisKeyBuilder,
) : AttemptLimitManager {

    /**
     * 加载 Lua 脚本并缓存其 SHA 值，供后续调用使用。
     */
    private val sha = lua.load("lua/attempt_limit.lua")

    /**
     * 构造尝试次数记录的 Redis 键。
     *
     * @param keyId 唯一标识符，用于区分不同资源的尝试记录。
     * @return Redis 键字符串。
     */
    private fun attemptsKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("attempts", keyId))

    /**
     * 构造锁定状态记录的 Redis 键。
     *
     * @param keyId 唯一标识符，用于区分不同资源的锁定状态。
     * @return Redis 键字符串。
     */
    private fun lockKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("lock", keyId))

    /**
     * 构造失败计数（打击次数）记录的 Redis 键。
     *
     * @param keyId 唯一标识符，用于区分不同资源的失败计数。
     * @return Redis 键字符串。
     */
    private fun strikesKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("strikes", keyId))

    /**
     * 解析 Lua 脚本返回的结果。
     *
     * Lua 返回值格式为：[allowed, attempts_ttl_ms, lock_ttl_ms, remaining_attempts, capacity]
     *
     * @param raw Lua 脚本返回的原始结果。
     * @return 解析后的 AttemptLimitResult 对象。
     * @throws IllegalArgumentException 当返回值数量不足 5 个时抛出异常。
     */
    private fun parseResult(raw: Any?): AttemptLimitResult {
        val list = when (raw) {
            is List<*> -> raw
            is Array<*> -> raw.toList()
            else -> error("Unexpected Lua return: ${raw?.javaClass}")
        }
        require(list.size >= 5) { "Lua result size < 5: $list" }

        val allowed = ConversionUtils.asLong(list[0]) != 0L
        val attemptsTtl = ConversionUtils.asLong(list[1])
        val lockTtl = ConversionUtils.asLong(list[2])
        val remainingAttempts = ConversionUtils.asLong(list[3])
        val capacity = ConversionUtils.asLong(list[4])

        return AttemptLimitResult(
            allowed = allowed,
            attemptsTtlMs = attemptsTtl,
            lockTtlMs = lockTtl,
            remainingAttempts = remainingAttempts,
            capacity = capacity
        )
    }

    /**
     * 处理失败事件，更新尝试次数、锁定状态和失败计数，并返回检查结果。
     *
     * @param key 资源标识键。
     * @param policy 尝试限制策略。
     * @return AttemptCheck 检查结果对象。
     */
    override fun onFail(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()
        val res = lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),  // ARGV[1] window_ms
                policy.max,                // ARGV[2] capacity
                policy.lock.toMillis(),    // ARGV[3] base_lock_ms
                policy.escalate.toCsv(),   // ARGV[4] escalate "th=ms;th=ms"
                0,                         // ARGV[5] is_success
                1,                         // ARGV[6] strike_inc
                0                          // ARGV[7] peek_only
            )
        )
        val r = parseResult(res)
        return AttemptCheck(
            allowed = r.allowed,
            attemptsTtlMs = if (r.allowed) r.attemptsTtlMs else 0L,
            lockTtlMs = r.lockTtlMs,
            remainingAttempts = r.remainingAttempts,
            capacity = r.capacity
        )
    }

    /**
     * 处理成功事件，清除尝试次数和锁定状态，并重置相关计数。
     *
     * @param key 资源标识键。
     * @param policy 尝试限制策略。
     * @return AttemptCheck 成功状态的检查结果对象。
     */
    override fun onSuccess(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()
        lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),  // ARGV[1] window_ms
                0,                         // ARGV[2] N/A
                0,                         // ARGV[3] N/A
                "",                        // ARGV[4] N/A
                1,                         // ARGV[5] is_success => 清 attempts / lock
                0,                         // ARGV[6] strike_inc (ignored)
                0                          // ARGV[7] peek_only (ignored)
            )
        )
        return AttemptCheck(
            allowed = true,
            attemptsTtlMs = 0,
            lockTtlMs = 0,
            remainingAttempts = policy.max, // 重置为满
            capacity = policy.max
        )
    }

    /**
     * 预检当前尝试状态，不消耗尝试次数，仅用于查询。
     *
     * @param key 资源标识键。
     * @param policy 尝试限制策略。
     * @return AttemptCheck 查询结果对象。
     */
    override fun onCheck(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()
        val res = lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),  // ARGV[1] window_ms
                policy.max,                // ARGV[2] capacity
                policy.lock.toMillis(),    // ARGV[3] base_lock_ms
                policy.escalate.toCsv(),   // ARGV[4] escalate "th=ms;th=ms"
                0,                         // ARGV[5] is_success = 0
                1,                         // ARGV[6] strike_inc = 1（预检耗尽即累计）
                1                          // ARGV[7] peek_only = 1（不消耗窗口）
            )
        )
        val r = parseResult(res)
        return AttemptCheck(
            allowed = r.allowed,
            attemptsTtlMs = if (r.allowed) r.attemptsTtlMs else 0L,
            lockTtlMs = r.lockTtlMs,
            remainingAttempts = r.remainingAttempts,
            capacity = r.capacity
        )
    }
}

