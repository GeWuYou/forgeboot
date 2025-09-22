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
    private val proxyManager: ProxyManager<String>, // 由 Bucket4j-Redisson 构建
    private val redisson: RedissonClient,
    private val keyBuilder: RedisKeyBuilder,
) : AttemptLimitManager {
    /**
     * 获取指定作用域的尝试次数桶代理对象。
     *
     * @param keyId 作用域标识符
     * @param policy 当前策略，用于构建桶配置
     * @return BucketProxy 桶代理对象
     */
    private fun attemptsBucket(keyId: String, policy: AttemptPolicy): BucketProxy {
        val redisKey = attemptsKey(keyId)
        return proxyManager.getProxy(redisKey) { bucketConfig(policy) }
    }

    /**
     * 构建桶配置，基于策略中的最大尝试次数和窗口时间。
     *
     * @param policy 尝试策略
     * @return BucketConfiguration 桶配置对象
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
     * 获取指定 keyId 对应的锁定状态桶。
     *
     * @param keyId 作用域标识符
     * @return RBucket<Long> Redis 桶对象
     */
    private fun lockBucket(keyId: String): RBucket<Long> =
        redisson.getBucket(lockKey(keyId))

    /**
     * 获取指定 keyId 对应的失败计数器。
     *
     * @param keyId 作用域标识符
     * @return RAtomicLong Redis 原子长整型计数器
     */
    private fun strikesCounter(keyId: String): RAtomicLong =
        redisson.getAtomicLong(strikesKey(keyId))

    /**
     * 构造尝试次数桶的 Redis 键名。
     *
     * @param scopeValue 作用域值
     * @return String Redis 键名
     */
    private fun attemptsKey(scopeValue: String): String =
        keyBuilder.attemptLimit(Key("attempts", scopeValue))

    /**
     * 构造锁定桶的 Redis 键名。
     *
     * @param scopeValue 作用域值
     * @return String Redis 键名
     */
    private fun lockKey(scopeValue: String): String =
        keyBuilder.attemptLimit(Key("lock", scopeValue))

    /**
     * 构造失败计数器的 Redis 键名。
     *
     * @param principalValue 主体值
     * @return String Redis 键名
     */
    private fun strikesKey(principalValue: String): String =
        keyBuilder.attemptLimit(Key("strikes", principalValue))

    /**
     * 锁 TTL 只延长不缩短：现有 TTL < newLockMs 时，更新 TTL；否则保持不变。
     *
     * @param lock Redis 桶对象
     * @param newLockMs 新的锁定时间（毫秒）
     */
    private fun extendLockIfLonger(lock: RBucket<Long>, newLockMs: Long) {
        val ttl = lock.remainTimeToLive()
        when {
            ttl == -1L -> return // 永久锁，不动
            ttl <= 0L -> {
                lock[System.currentTimeMillis() + newLockMs] = Duration.ofMillis(newLockMs)
            }

            ttl < newLockMs -> {
                // 直接以新的 TTL 覆盖（只会更长）
                lock[System.currentTimeMillis() + newLockMs] = Duration.ofMillis(newLockMs)
            }

            else -> {
                // 现有更长，忽略
            }
        }
    }

    /**
     * 根据策略和失败次数计算新的锁定时间。
     *
     * @param policy 尝试策略
     * @param strikes 当前失败次数
     * @return Long 锁定时间（毫秒）
     */
    private fun computeLockMs(policy: AttemptPolicy, strikes: Long): Long {
        var ms = policy.lock.toMillis()
        if (policy.escalate.isNotEmpty()) {
            // 升序按阈值匹配最长锁
            policy.escalate.toSortedMap().forEach { (th, dur) ->
                if (strikes >= th) ms = max(ms, dur.toMillis())
            }
        }
        return ms
    }

    /**
     * 规范化 Redisson remainTimeToLive：
     * -1 表示永久 → 返回 0（无意义）
     * >0 正常毫秒
     * 0 或负数视为无 TTL
     *
     * @param ttl Redisson 返回的剩余生存时间
     * @return Long 规范化后的 TTL 值
     */
    private fun normalizeTtl(ttl: Long): Long =
        if (ttl > 0) ttl else 0L

    /**
     * 处理失败情况下的尝试次数检查
     *
     * 当操作失败时调用此方法，用于更新和检查尝试次数限制。
     * 该方法通过令牌桶机制控制尝试频率，并在超过限制时施加锁定及阶梯式惩罚。
     *
     * @param key 操作的唯一标识键，用于区分不同的操作实例
     * @param policy 尝试策略，定义限制规则和行为（如最大尝试次数、锁定时间等）
     * @return AttemptCheck 尝试检查结果，包含是否允许继续尝试、剩余尝试时间、锁定时间等信息
     */
    override fun onFail(
        key: Key,
        policy: AttemptPolicy,
    ): AttemptCheck {
        val keyId = key.full()

        var attemptsTtlMs = 0L
        var lockTtlMs: Long

        // 1) 锁优先：如果当前操作已被锁定，则直接拒绝尝试
        val lock = lockBucket(keyId)
        val currentLockTtl = lock.remainTimeToLive()
        if (currentLockTtl > 0L || currentLockTtl == -1L) {
            lockTtlMs = normalizeTtl(currentLockTtl)
            return AttemptCheck(false, attemptsTtlMs, lockTtlMs)
        }

        // 2) 失败计数：尝试从令牌桶中消费一个令牌以允许继续尝试
        val bucket = attemptsBucket(keyId, policy)
        val consumed = bucket.tryConsume(1)
        if (consumed) {
            // 计算下一次可尝试的时间（即下一个令牌生成所需等待时间）
            val waitNs = bucket.estimateAbilityToConsume(1).nanosToWaitForRefill
            attemptsTtlMs = Duration.ofNanos(waitNs).toMillis()
            return AttemptCheck(true, attemptsTtlMs, 0)
        }

        // 3) 令牌桶耗尽 → 触发锁定并根据失败次数实施阶梯惩罚
        val strikesVal = incrementStrikesAndEnsureTtl(keyId)


        // 根据策略和失败次数计算新的锁定时长，并更新锁定状态
        val newLockMs = computeLockMs(policy, strikesVal)
        extendLockIfLonger(lock, newLockMs)

        // 取当前锁剩余时间和新锁时间的最大值作为最终锁定时间
        lockTtlMs = max(lock.remainTimeToLive(), newLockMs)

        return AttemptCheck(false, 0, normalizeTtl(lockTtlMs))
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
        // 1) 清锁
        lockBucket(keyId).delete()
        // 2) 桶重置为“满”
        val bucket = attemptsBucket(keyId, policy)
        bucket.replaceConfiguration(bucketConfig(policy), TokensInheritanceStrategy.RESET)
        // strikes 删除
        strikesCounter(keyId).delete()
        // strikes 是否清零？通常不清（长期惩罚更符合安全策略）。
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

        // 1) 先看是否已锁
        val lock = lockBucket(keyId)
        val currentLockTtl = lock.remainTimeToLive()
        if (currentLockTtl > 0L || currentLockTtl == -1L) {
            return AttemptCheck(
                allowed = false,
                attemptsTtlMs = 0,
                lockTtlMs = normalizeTtl(currentLockTtl)
            )
        }

        // 2) 估算是否还有“失败额度”（不消耗令牌）
        val bucket = attemptsBucket(keyId, policy)
        val estimation = bucket.estimateAbilityToConsume(1)
        val waitNs = estimation.nanosToWaitForRefill

        if (waitNs <= 0L) {
            // 还有额度 → 放行进入方法；真正失败时再在 onFail(...) 里消耗令牌
            return AttemptCheck(
                allowed = true,
                attemptsTtlMs = 0,
                lockTtlMs = 0
            )
        }

        // 3) 已无额度 → 直接在预检阶段执行“耗尽”逻辑：累计 strikes + 计算并设置锁
        val strikesVal = incrementStrikesAndEnsureTtl(keyId)
        val newLockMs = computeLockMs(policy, strikesVal)
        extendLockIfLonger(lock, newLockMs)
        val finalLockTtl = max(lock.remainTimeToLive(), newLockMs)

        return AttemptCheck(
            allowed = false,
            attemptsTtlMs = Duration.ofNanos(waitNs).toMillis(), // 信息用途，已上锁时客户端通常以锁 TTL 为准
            lockTtlMs = normalizeTtl(finalLockTtl)
        )
    }

    /**
     * 增加失败计数并确保其不会过期
     *
     * @param keyId 作用域标识符
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
