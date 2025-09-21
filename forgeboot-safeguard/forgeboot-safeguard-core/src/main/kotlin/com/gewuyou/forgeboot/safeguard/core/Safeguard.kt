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

package com.gewuyou.forgeboot.safeguard.core

import com.gewuyou.forgeboot.safeguard.core.api.CooldownGuard
import com.gewuyou.forgeboot.safeguard.core.api.IdempotencyManager
import com.gewuyou.forgeboot.safeguard.core.api.RateLimiter
import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus
import com.gewuyou.forgeboot.safeguard.core.exception.CooldownActiveException
import com.gewuyou.forgeboot.safeguard.core.exception.IdempotencyConflictException
import com.gewuyou.forgeboot.safeguard.core.exception.RateLimitExceededException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.IdempotencyRecord
import com.gewuyou.forgeboot.safeguard.core.policy.CooldownPolicy
import com.gewuyou.forgeboot.safeguard.core.policy.IdempotencyPolicy
import com.gewuyou.forgeboot.safeguard.core.policy.RateLimitPolicy
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec

/**
 * 保卫工具类，提供限流、冷却和幂等性控制功能。
 *
 * @since 2025-09-21 11:44:49
 * @author gewuyou
 */
object Safeguard {

    /**
     * 使用限流策略执行代码块。
     *
     * @param rateLimiter 限流器实例，用于判断是否允许执行。
     * @param metrics 指标收集器，用于记录限流事件。
     * @param key 限流键，标识当前操作的唯一性。
     * @param policy 限流策略配置。
     * @param block 要执行的业务逻辑代码块。
     * @return 执行结果。
     * @throws RateLimitExceededException 当请求被限流时抛出。
     */
    fun <T> withRateLimit(
        rateLimiter: RateLimiter,
        metrics: SafeguardMetrics,
        key: Key,
        policy: RateLimitPolicy,
        block: () -> T,
    ): T {
        val r = rateLimiter.tryConsume(key, policy)
        if (!r.allowed) {
            metrics.onRateLimitBlocked(key.namespace, key.value); throw RateLimitExceededException(key)
        }
        return block()
    }

    /**
     * 使用冷却策略执行代码块，并支持异常回滚。
     *
     * @param cooldown 冷却守卫实例，用于判断是否处于冷却期。
     * @param metrics 指标收集器，用于记录冷却事件。
     * @param key 冷却键，标识当前操作的唯一性。
     * @param policy 冷却策略配置。
     * @param rollbackOn 异常匹配函数，决定是否在特定异常下释放冷却锁。
     * @param block 要执行的业务逻辑代码块。
     * @return 执行结果。
     * @throws CooldownActiveException 当操作处于冷却期时抛出。
     */
    fun <T> withCooldown(
        cooldown: CooldownGuard,
        metrics: SafeguardMetrics,
        key: Key,
        policy: CooldownPolicy,
        rollbackOn: (Throwable) -> Boolean = { false },
        block: () -> T,
    ): T {
        val t = cooldown.acquire(key, policy)
        if (!t.acquired) {
            metrics.onCooldownBlocked(key.namespace, key.value); throw CooldownActiveException(key)
        }
        return try {
            block()
        } catch (ex: Throwable) {
            if (rollbackOn(ex)) cooldown.release(key)
            throw ex
        }
    }

    /**
     * 使用幂等性管理器执行代码块，确保操作只执行一次。
     *
     * @param idem 幂等性管理器，用于检查和记录幂等状态。
     * @param metrics 指标收集器，用于记录幂等命中/冲突情况。
     * @param codec 数据编解码器，用于序列化和反序列化结果。
     * @param key 幂等键，标识当前操作的唯一性。
     * @param policy 幂等策略配置。
     * @param block 要执行的业务逻辑代码块。
     * @return 执行结果。
     * @throws IdempotencyConflictException 当操作正在处理或冲突时抛出。
     */
    fun <T> withIdempotency(
        idem: IdempotencyManager,
        metrics: SafeguardMetrics,
        codec: PayloadCodec,
        key: Key,
        policy: IdempotencyPolicy,
        block: () -> T,
    ): T {
        // 检查是否已有记录
        idem[key]?.let { rec ->
            when (rec.status) {
                IdempotencyStatus.SUCCESS -> {
                    metrics.onIdemHit(key.namespace, key.value)
                    @Suppress("UNCHECKED_CAST")
                    return codec.deserialize(rec.payload, rec.payloadType) as T
                }

                IdempotencyStatus.PENDING -> {
                    metrics.onIdemConflict(key.namespace, key.value)
                    throw IdempotencyConflictException(key)
                }
            }
        }
        // 首次：抢占
        if (!idem.tryAcquirePending(key, policy)) {
            metrics.onIdemConflict(key.namespace, key.value)
            throw IdempotencyConflictException(key)
        }
        metrics.onIdemMiss(key.namespace, key.value)
        return try {
            val out = block()
            idem.saveSuccess(
                key,
                IdempotencyRecord(
                    status = IdempotencyStatus.SUCCESS,
                    payloadType = out?.javaClass?.name,
                    payload = codec.serialize(out)
                ),
                policy
            )
            out
        } catch (ex: Throwable) {
            idem.clear(key)
            throw ex
        }
    }
}
