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
 * 基于 Redis 和 Lua 脚本实现的尝试次数限制管理器。
 * 提供失败和成功两种场景下的尝试次数控制逻辑，支持作用域、时间窗口、最大尝试次数、锁定时长及升级惩罚策略。
 *
 * @property lua Lua 脚本执行器，用于加载和执行 Lua 脚本。
 * @property keyBuilder Redis 键构建器，用于生成 Redis 中使用的键。
 * @since 2025-09-22 10:17:59
 * @author gewuyou
 */
class LuaAttemptLimitManager(
    private val lua: LuaScriptExecutor,
    private val keyBuilder: RedisKeyBuilder,
) : AttemptLimitManager {
    private val sha = lua.load("lua/attempt_limit.lua")

    /**
     * 生成尝试次数限制的键值
     * @param keyId 键的标识符
     * @return 返回构建后的尝试次数限制键值字符串
     */
    private fun attemptsKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("attempts", keyId))

    /**
     * 生成锁定状态的键值
     * @param keyId 键的标识符
     * @return 返回构建后的锁定状态键值字符串
     */
    private fun lockKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("lock", keyId))

    /**
     * 生成违规次数的键值
     * @param keyId 键的标识符
     * @return 返回构建后的违规次数键值字符串
     */
    private fun strikesKey(keyId: String): String =
        keyBuilder.attemptLimit(Key("strikes", keyId))

    /**
     * 解析 Lua 脚本执行结果。
     *
     * @param raw Lua 脚本返回的原始数据，期望格式为包含3个元素的数组或列表：
     *           [allowed, attempts_ttl_ms, lock_ttl_ms]
     * @return AttemptLimitResult 解析后的尝试限制结果对象。
     */
    private fun parseResult(raw: Any?): AttemptLimitResult {
        // 验证并转换Lua返回值为列表格式
        val list = when (raw) {
            is List<*> -> raw
            is Array<*> -> raw.toList()
            else -> error("Unexpected Lua MULTI return: ${raw?.javaClass}")
        }
        require(list.size >= 3) { "Lua result size < 3: $list" }

        // 提取并转换各个字段值
        val allowed = ConversionUtils.asLong(list[0]) != 0L
        val attemptsTtl = ConversionUtils.asLong(list[1])
        val lockTtl = ConversionUtils.asLong(list[2])

        return AttemptLimitResult(allowed, attemptsTtl, lockTtl)
    }

    /**
     * 处理失败情况下的尝试次数检查
     *
     * 当操作失败时调用此方法，用于更新和检查尝试次数限制
     *
     * @param key 操作的唯一标识键，用于区分不同的操作实例
     * @param policy 尝试策略，定义限制规则和行为
     * @return AttemptCheck 尝试检查结果，包含是否允许继续尝试等信息
     */
    override fun onFail(
        key: Key,
        policy: AttemptPolicy,
    ): AttemptCheck {
        val keyId = key.full()
        val res = lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),       // ARGV[1]
                policy.max,                     // ARGV[2]
                policy.lock.toMillis(),         // ARGV[3]
                policy.escalate.toCsv(),        // ARGV[4]
                0,                              // ARGV[5] is_success
                1                               // ARGV[6] strike_inc（唯一 Key，记一次即可）
            )
        )
        val r = parseResult(res)
        return AttemptCheck(
            allowed = r.allowed,
            attemptsTtlMs = if (r.allowed) r.attemptsTtlMs else 0L,
            lockTtlMs = r.lockTtlMs
        )
    }

    /**
     * 处理成功情况下的尝试次数检查
     *
     * 当操作成功时调用此方法，用于重置或更新尝试次数状态
     *
     * @param key 操作的唯一标识键，用于区分不同的操作实例
     * @param policy 尝试策略，定义限制规则和行为
     * @return AttemptCheck 尝试检查结果，包含是否允许继续尝试等信息
     */
    override fun onSuccess(
        key: Key,
        policy: AttemptPolicy,
    ): AttemptCheck {
        val keyId = key.full()
        lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),  // ARGV[1]
                0,                         // ARGV[2] 无意义
                0,                         // ARGV[3] 无意义
                "",                        // ARGV[4] 无意义
                1,                         // ARGV[5] is_success → 清 attempts/lock
                0                          // ARGV[6] strike_inc 无意义
            )
        )
        return AttemptCheck(true, 0, 0)
    }

    /**
     * 检查指定键值是否符合给定的尝试策略
     *
     * @param key 需要检查的键值对象
     * @param policy 尝试策略，用于定义检查规则
     * @return 返回检查结果，包含是否通过检查以及相关的尝试信息
     */
    override fun onCheck(
        key: Key,
        policy: AttemptPolicy,
    ): AttemptCheck {
        val keyId = key.full()
        val res = lua.evalSha(
            sha,
            keys = listOf(
                attemptsKey(keyId),
                lockKey(keyId),
                strikesKey(keyId)
            ),
            args = listOf(
                policy.window.toMillis(),       // ARGV[1]
                policy.max,                     // ARGV[2]
                policy.lock.toMillis(),         // ARGV[3]
                policy.escalate.toCsv(),        // ARGV[4]
                0,                              // ARGV[5] is_success = 0
                1,                              // ARGV[6] strike_inc = 1（预检已耗尽时就累计并加锁）
                1                               // ARGV[7] peek_only = 1（不消耗窗口）
            )
        )
        val r = parseResult(res)
        return AttemptCheck(
            allowed = r.allowed,
            attemptsTtlMs = if (r.allowed) r.attemptsTtlMs else 0L,
            lockTtlMs = r.lockTtlMs
        )
    }
}
