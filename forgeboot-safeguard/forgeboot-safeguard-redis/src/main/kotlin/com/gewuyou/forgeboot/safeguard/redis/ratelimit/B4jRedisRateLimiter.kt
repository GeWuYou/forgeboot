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

import com.gewuyou.forgeboot.safeguard.core.api.RateLimiter
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.model.RateLimitResult
import com.gewuyou.forgeboot.safeguard.core.policy.RateLimitPolicy
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.proxy.ProxyManager
import java.time.Instant

/**
 * Redis实现的限流器类
 * 使用Redisson代理管理器实现分布式限流功能
 *
 * @property proxyManager Redisson代理管理器，用于构建分布式令牌桶
 * @since 2025-09-21 12:25:19
 * @author gewuyou
 */
class B4jRedisRateLimiter(
    private val proxyManager: ProxyManager<String>,
) : RateLimiter {

    /**
     * 尝试消费一个令牌，执行限流检查
     *
     * @param key 限流键值，用于标识不同的限流对象
     * @param policy 限流策略，包含容量和 refill 规则
     * @return RateLimitResult 限流结果，包含是否允许、剩余令牌数和重置时间
     */
    override fun tryConsume(key: Key, policy: RateLimitPolicy): RateLimitResult {
        // 构建令牌桶配置
        val conf = BucketConfiguration.builder()
            .addLimit(
                Bandwidth.builder()
                    .capacity(policy.capacity)
                    .refillIntervally(policy.refillTokens, policy.refillPeriod)
                    .build(),
            )
            .build()

        // 创建或获取对应的令牌桶实例
        val bucket = proxyManager.builder().build(key.full()) { conf }
        return if (policy.timeout.isZero) {
            // 尝试消费设置令牌并获取剩余信息
            val probe = bucket.tryConsumeAndReturnRemaining(policy.requested)

            // 计算下次重置时间
            val reset = if (!probe.isConsumed && probe.nanosToWaitForRefill > 0)
                Instant.now().plusNanos(probe.nanosToWaitForRefill) else null
            RateLimitResult(
                allowed = probe.isConsumed,
                remaining = probe.remainingTokens,
                resetAt = reset
            )
        } else {
            // 阻塞式获取（有上限）
            val ok = bucket.asBlocking().tryConsume(policy.requested, policy.timeout)
            // 拿当前余量：消费 0 个可获得剩余值，不改变桶状态
            val after = bucket.tryConsumeAndReturnRemaining(0)
            RateLimitResult(
                allowed = ok,
                remaining = after.remainingTokens,
                resetAt = null // 已经在超时时间内等到了，不再给 resetAt
            )
        }
    }


    /**
     * 返还指定数量的令牌到令牌桶中
     *
     * @param key 限流键值，用于标识不同的限流对象
     * @param requested 请求退还的令牌数量
     * @param policy 限流策略，包含容量和 refill 规则
     */
    fun refund(key: Key, requested: Long, policy: RateLimitPolicy) {
        val conf = BucketConfiguration.builder()
            .addLimit(
                Bandwidth.builder()
                    .capacity(policy.capacity)
                    .refillIntervally(policy.refillTokens, policy.refillPeriod)
                    .build()
            ).build()

        // 使用代理管理器构建指定key的令牌桶实例
        val bucket = proxyManager.builder().build(key.full()) { conf }
        // 如果有预消费令牌的需求，则执行令牌消费操作
        if (requested > 0) {
            // 保护：最多补到容量上限（Bucket4j 内部也会处理不超过容量，这里显式一下）
            val add = requested.coerceAtMost(policy.capacity)
            bucket.addTokens(add)
        }
    }
}
