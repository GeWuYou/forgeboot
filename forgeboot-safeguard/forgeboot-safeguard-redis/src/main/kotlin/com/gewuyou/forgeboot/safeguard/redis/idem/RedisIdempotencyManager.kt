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

package com.gewuyou.forgeboot.safeguard.redis.idem

import com.gewuyou.forgeboot.safeguard.core.api.IdempotencyManager
import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.IdempotentRecord
import com.gewuyou.forgeboot.safeguard.core.policy.IdempotentPolicy
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import com.gewuyou.forgeboot.safeguard.redis.support.LuaScriptExecutor
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.*

/**
 * Redis 幂等性管理器，用于基于 Redis 实现接口的幂等控制。
 * 通过 Lua 脚本保证操作的原子性，支持获取、尝试获取、保存成功状态、清除和更新状态等操作。
 *
 * @property redis Redis 操作模板，用于与 Redis 进行交互。
 * @property keyBuilder Redis 键构建器，用于生成幂等相关的 Redis 键。
 * @property lua Lua 脚本执行器，用于加载和执行 Lua 脚本。
 * @since 2025-09-21 12:52:03
 * @author gewuyou
 */
class RedisIdempotencyManager(
    private val redis: StringRedisTemplate,
    private val keyBuilder: RedisKeyBuilder,
    private val lua: LuaScriptExecutor,
) : IdempotencyManager {

    // 加载幂等获取和成功的 Lua 脚本
    private val acquireSha = lua.load("lua/idem_acquire.lua")
    private val successSha = lua.load("lua/idem_success.lua")

    /**
     * 根据指定的键获取对应的幂等记录。
     *
     * @param key 幂等键，用于标识一次请求。
     * @return 幂等记录，如果不存在则返回 null。
     */
    override fun get(key: Key): IdempotentRecord? {
        val k = keyBuilder.idem(key)
        val map: Map<String, String> = redis.opsForHash<String, String>().entries(k)
        if (map.isEmpty()) return null
        val status = map["status"]?.let { IdempotencyStatus.valueOf(it) } ?: return null
        val type = map["type"]
        val bodyB64 = map["body"]
        val payload = bodyB64?.let { Base64.getDecoder().decode(it) }
        return IdempotentRecord(status = status, payloadType = type, payload = payload)
    }

    /**
     * 尝试获取指定键的待处理状态，如果成功则表示可以继续处理该请求。
     *
     * @param key 幂等键，用于标识一次请求。
     * @param policy 幂等策略，包含超时时间等配置。
     * @return 如果成功获取待处理状态则返回 true，否则返回 false。
     */
    override fun tryAcquirePending(key: Key, policy: IdempotentPolicy): Boolean {
        val k = keyBuilder.idem(key)
        val ret = lua.evalShaToLong(
            sha = acquireSha,
            keys = listOf(k),
            args = listOf(policy.ttl.seconds.toString())
        )
        return ret == 1L
    }

    /**
     * 保存指定键的成功状态及响应数据。
     *
     * @param key 幂等键，用于标识一次请求。
     * @param record 幂等记录，包含状态、响应类型和响应体。
     * @param policy 幂等策略，包含超时时间等配置。
     */
    override fun saveSuccess(key: Key, record: IdempotentRecord, policy: IdempotentPolicy) {
        val k = keyBuilder.idem(key)
        val payloadB64 = record.payload?.let { Base64.getEncoder().encodeToString(it) } ?: ""
        lua.evalShaToLong(
            sha = successSha,
            keys = listOf(k),
            args = listOf(
                policy.ttl.seconds.toString(),
                record.payloadType ?: "",
                payloadB64
            )
        )
    }

    /**
     * 清除指定键的幂等记录。
     *
     * @param key 幂等键，用于标识一次请求。
     */
    override fun clear(key: Key) {
        redis.delete(keyBuilder.idem(key))
    }

    /**
     * 更新指定键的幂等状态，并刷新其过期时间。
     *
     * @param key 幂等键，用于标识一次请求。
     * @param status 新的状态。
     * @param policy 幂等策略，包含超时时间等配置。
     */
    override fun updateStatus(key: Key, status: IdempotencyStatus, policy: IdempotentPolicy) {
        val k = keyBuilder.idem(key)
        redis.opsForHash<String, String>().put(k, "status", status.name)
        // 续命，确保在 TTL 内可见
        redis.expire(k, policy.ttl)
    }
}

