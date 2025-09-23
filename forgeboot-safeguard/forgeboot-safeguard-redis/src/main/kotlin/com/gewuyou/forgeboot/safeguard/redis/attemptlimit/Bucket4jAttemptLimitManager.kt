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
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.AttemptCheck
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import com.gewuyou.forgeboot.safeguard.redis.key.RedisKeyBuilder
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.TokensInheritanceStrategy
import io.github.bucket4j.distributed.BucketProxy
import io.github.bucket4j.distributed.proxy.ProxyManager
import org.redisson.api.RAtomicLong
import org.redisson.api.RBucket
import org.redisson.api.RedissonClient
import java.time.Duration
import kotlin.math.max

/**
 * 尝试限制管理器，基于 Bucket4j 和 Redisson 实现分布式限流与失败惩罚机制。
 *
 * @since 2025-09-22 11:33:39
 * @author gewuyou
 *
 * @property proxyManager 用于获取 BucketProxy 的代理管理器，由 Bucket4j-Redisson 构建
 * @property redisson Redisson 客户端，用于操作 Redis 中的锁、计数器等数据结构
 * @property keyBuilder 键构建器，用于生成 Redis 中各组件的键名
 */
class Bucket4jAttemptLimitManager(
    private val proxyManager: ProxyManager<String>,
    private val redisson: RedissonClient,
    private val keyBuilder: RedisKeyBuilder,
) : AttemptLimitManager {

    /** ---- helpers --------------------------------------------------------- */

    /**
     * 获取指定 keyId 对应的尝试次数桶（BucketProxy）。
     *
     * @param keyId 唯一标识符，用于构建 Redis 键
     * @param policy 尝试策略配置
     * @return BucketProxy 实例
     */
    private fun attemptsBucket(keyId: String, policy: AttemptPolicy): BucketProxy {
        val redisKey = attemptsKey(keyId)
        return proxyManager.getProxy(redisKey) { bucketConfig(policy) }
    }

    /**
     * 构建 Bucket4j 的桶配置。
     *
     * @param policy 尝试策略配置
     * @return BucketConfiguration 配置对象
     */
    private fun bucketConfig(policy: AttemptPolicy): BucketConfiguration {
        return BucketConfiguration.builder()
            .addLimit(
                Bandwidth.builder()
                    .capacity(policy.max.toLong())
                    .refillGreedy(policy.max.toLong(), policy.window)
                    .build()
            )
            .build()
    }

    /**
     * 获取指定 keyId 对应的锁桶（RBucket<Long>）。
     *
     * @param keyId 唯一标识符，用于构建 Redis 键
     * @return RBucket<Long> 实例
     */
    private fun lockBucket(keyId: String): RBucket<Long> =
        redisson.getBucket(lockKey(keyId))

    /**
     * 获取指定 keyId 对应的失败计数器（RAtomicLong）。
     *
     * @param keyId 唯一标识符，用于构建 Redis 键
     * @return RAtomicLong 实例
     */
    private fun strikesCounter(keyId: String): RAtomicLong =
        redisson.getAtomicLong(strikesKey(keyId))

    /**
     * 构建尝试次数桶的 Redis 键名。
     *
     * @param scopeValue 作用域值
     * @return Redis 键名字符串
     */
    private fun attemptsKey(scopeValue: String): String =
        keyBuilder.attemptLimit(Key("attempts", scopeValue))

    /**
     * 构建锁桶的 Redis 键名。
     *
     * @param scopeValue 作用域值
     * @return Redis 键名字符串
     */
    private fun lockKey(scopeValue: String): String =
        keyBuilder.attemptLimit(Key("lock", scopeValue))

    /**
     * 构建失败计数器的 Redis 键名。
     *
     * @param principalValue 主体值
     * @return Redis 键名字符串
     */
    private fun strikesKey(principalValue: String): String =
        keyBuilder.attemptLimit(Key("strikes", principalValue))

    /**
     * 如果当前锁的 TTL 小于新锁时间，则延长锁的有效期。
     *
     * @param lock 锁桶实例
     * @param newLockMs 新锁时间（毫秒）
     */
    private fun extendLockIfLonger(lock: RBucket<Long>, newLockMs: Long) {
        val ttl = lock.remainTimeToLive()
        when {
            ttl == -1L -> return // 永久锁
            ttl <= 0L -> {
                lock[System.currentTimeMillis() + newLockMs] = Duration.ofMillis(newLockMs)
            }
            ttl < newLockMs -> {
                lock[System.currentTimeMillis() + newLockMs] = Duration.ofMillis(newLockMs)
            }
            else -> Unit
        }
    }

    /**
     * 根据失败次数计算应加锁的时间（支持阶梯惩罚）。
     *
     * @param policy 尝试策略配置
     * @param strikes 当前失败次数
     * @return 应加锁的毫秒数
     */
    private fun computeLockMs(policy: AttemptPolicy, strikes: Long): Long {
        var ms = policy.lock.toMillis()
        if (policy.escalate.isNotEmpty()) {
            policy.escalate.toSortedMap().forEach { (th, dur) ->
                if (strikes >= th) ms = max(ms, dur.toMillis())
            }
        }
        return ms
    }

    /**
     * 规范化 TTL 值，确保非负。
     *
     * @param ttl 原始 TTL 值
     * @return 规范化后的 TTL 值
     */
    private fun normalizeTtl(ttl: Long): Long = if (ttl > 0) ttl else 0L

    /**
     * 获取策略中定义的最大尝试次数。
     *
     * @param policy 尝试策略配置
     * @return 最大尝试次数（Long 类型）
     */
    private fun capacityOf(policy: AttemptPolicy): Long = policy.max.toLong()

    /**
     * 获取当前可用剩余尝试次数（不消耗令牌）。
     *
     * @param bucket 桶实例
     * @param policy 尝试策略配置
     * @return 可用剩余尝试次数
     */
    private fun remainingAttemptsOf(bucket: BucketProxy, policy: AttemptPolicy): Long {
        // Bucket4j 支持读取可用令牌数；如你的版本不支持，可用 tryConsumeAndReturnRemaining(0) 兜底。
        val available = bucket.availableTokens // 等价于 getAvailableTokens()
        return available.coerceIn(0, capacityOf(policy))
    }

    /** ---- AttemptLimitManager --------------------------------------------- */

    /**
     * 处理失败尝试，更新尝试次数和锁状态。
     *
     * @param key 唯一键标识
     * @param policy 尝试策略配置
     * @return AttemptCheck 检查结果
     */
    override fun onFail(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()

        var attemptsTtlMs = 0L

        // 1) 锁优先
        val lock = lockBucket(keyId)
        val currentLockTtl = lock.remainTimeToLive()
        if (currentLockTtl > 0L || currentLockTtl == -1L) {
            val bucket = attemptsBucket(keyId, policy)
            val remaining = remainingAttemptsOf(bucket, policy)
            return AttemptCheck(
                allowed = false,
                attemptsTtlMs = attemptsTtlMs,
                lockTtlMs = normalizeTtl(currentLockTtl),
                remainingAttempts = remaining,
                capacity = capacityOf(policy)
            )
        }

        // 2) 消耗一次失败额度
        val bucket = attemptsBucket(keyId, policy)
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            val remaining = probe.remainingTokens.coerceIn(0, capacityOf(policy))
            val waitNs = bucket.estimateAbilityToConsume(1).nanosToWaitForRefill
            attemptsTtlMs = Duration.ofNanos(waitNs).toMillis()
            return AttemptCheck(
                allowed = true,
                attemptsTtlMs = attemptsTtlMs,
                lockTtlMs = 0,
                remainingAttempts = remaining,
                capacity = capacityOf(policy)
            )
        }

        // 3) 耗尽 → 累计 strikes 并加锁（阶梯惩罚）
        val strikesVal = incrementStrikesAndEnsureTtl(keyId)
        val newLockMs = computeLockMs(policy, strikesVal)
        extendLockIfLonger(lock, newLockMs)
        val finalLockTtl = max(lock.remainTimeToLive(), newLockMs)

        // 耗尽时剩余次数为 0
        return AttemptCheck(
            allowed = false,
            attemptsTtlMs = 0,
            lockTtlMs = normalizeTtl(finalLockTtl),
            remainingAttempts = 0,
            capacity = capacityOf(policy)
        )
    }

    /**
     * 处理成功尝试，清除锁和失败计数，并重置尝试次数桶。
     *
     * @param key 唯一键标识
     * @param policy 尝试策略配置
     * @return AttemptCheck 检查结果
     */
    override fun onSuccess(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()
        // 1) 清锁
        lockBucket(keyId).delete()
        // 2) 桶重置为“满”
        val bucket = attemptsBucket(keyId, policy)
        bucket.replaceConfiguration(bucketConfig(policy), TokensInheritanceStrategy.RESET)
        // 3) 清除 strikes 计数（可按策略选择保留，这里选择清除）
        strikesCounter(keyId).delete()

        return AttemptCheck(
            allowed = true,
            attemptsTtlMs = 0,
            lockTtlMs = 0,
            remainingAttempts = capacityOf(policy),
            capacity = capacityOf(policy)
        )
    }

    /**
     * 预检尝试次数状态，不消耗额度。
     *
     * @param key 唯一键标识
     * @param policy 尝试策略配置
     * @return AttemptCheck 检查结果
     */
    override fun onCheck(key: Key, policy: AttemptPolicy): AttemptCheck {
        val keyId = key.full()

        // 1) 先看锁
        val lock = lockBucket(keyId)
        val currentLockTtl = lock.remainTimeToLive()
        if (currentLockTtl > 0L || currentLockTtl == -1L) {
            val bucket = attemptsBucket(keyId, policy)
            val remaining = remainingAttemptsOf(bucket, policy)
            return AttemptCheck(
                allowed = false,
                attemptsTtlMs = 0,
                lockTtlMs = normalizeTtl(currentLockTtl),
                remainingAttempts = remaining, // 提供信息给前端，可视化展示
                capacity = capacityOf(policy)
            )
        }

        // 2) 估算是否还能“失败一次”（不消耗令牌）
        val bucket = attemptsBucket(keyId, policy)
        val estimation = bucket.estimateAbilityToConsume(1)
        val waitNs = estimation.nanosToWaitForRefill

        if (waitNs <= 0L) {
            val remaining = remainingAttemptsOf(bucket, policy)
            return AttemptCheck(
                allowed = true,
                attemptsTtlMs = 0,
                lockTtlMs = 0,
                remainingAttempts = remaining,
                capacity = capacityOf(policy)
            )
        }

        // 3) 已无额度 → 预检阶段直接累计 strikes 并触发锁（硬管控）
        val strikesVal = incrementStrikesAndEnsureTtl(keyId)
        val newLockMs = computeLockMs(policy, strikesVal)
        extendLockIfLonger(lock, newLockMs)
        val finalLockTtl = max(lock.remainTimeToLive(), newLockMs)

        return AttemptCheck(
            allowed = false,
            attemptsTtlMs = Duration.ofNanos(waitNs).toMillis(), // 信息用途；retryAfterMs 会优先用 lockTtlMs
            lockTtlMs = normalizeTtl(finalLockTtl),
            remainingAttempts = 0,
            capacity = capacityOf(policy)
        )
    }

    /**
     * 失败次数 +1，并确保 30 天不过期（你可按需调整策略）。
     *
     * @param keyId 唯一标识符
     * @return 当前失败次数
     */
    private fun incrementStrikesAndEnsureTtl(keyId: String): Long {
        val strikes = strikesCounter(keyId)
        return strikes.incrementAndGet().also {
            if (strikes.remainTimeToLive() <= 0L) {
                strikes.expire(Duration.ofDays(30))
            }
        }
    }
}
