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

package com.gewuyou.forgeboot.safeguard.autoconfigure.aop

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.RateLimit
import com.gewuyou.forgeboot.safeguard.autoconfigure.key.KeyResolutionSupport
import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.api.RateLimiter
import com.gewuyou.forgeboot.safeguard.core.exception.RateLimitExceededException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.policy.RateLimitPolicy
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.annotation.Order
import java.time.Duration

/**
 * 限流切面类，用于处理带有 @RateLimit 注解的方法调用。
 *
 * 该切面通过解析注解中的 SpEL 表达式获取限流参数，并使用 RateLimiter 实现进行限流控制。
 * 如果请求被限流，则抛出 RateLimitExceededException 异常。
 * 在方法执行失败并满足退款条件时，会尝试退还已消费的令牌。
 *
 * @property limiter 限流器实例，用于执行具体限流逻辑
 * @property beanFactory Spring Bean 工厂，用于解析 SpEL 表达式中的上下文变量
 *
 * @since 2025-09-21 14:20:01
 * @author gewuyou
 */
@Aspect
@Order(0)
class RateLimitAspect(
    private val limiter: RateLimiter,
    private val beanFactory: BeanFactory,
    private val metrics: SafeguardMetrics = NoopSafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
) {
    private companion object {
        const val NS = "safeguard:rl"
    }

    /**
     * 环绕通知方法，处理带有 @RateLimit 注解的方法调用。
     *
     * 该方法会解析注解中的 SpEL 表达式，构建限流策略和键，并执行限流判断。
     * 若限流通过则继续执行目标方法；若限流未通过则抛出异常。
     * 方法执行过程中若发生异常且满足退款条件，则尝试退还令牌。
     *
     * @param pjp 切入点，表示被拦截的方法
     * @param rl 限流注解，包含限流相关配置
     * @return 目标方法的返回值
     * @throws RateLimitExceededException 当请求超过限流阈值时抛出
     * @throws Throwable 当目标方法执行过程中抛出异常时传播该异常
     */
    @Around("@annotation(rl)")
    fun around(pjp: ProceedingJoinPoint, rl: RateLimit): Any? {
        // 解析限流参数
        val key: Key = keySupport.resolveForPjp(
            pjp, rl.resolverBean, rl.template, rl.key, defaultNamespace = NS
        )
        val cap = SpelEval.eval(pjp, beanFactory, rl.capacity, Number::class.java).toLong()
        val refill = SpelEval.eval(pjp, beanFactory, rl.refillTokens, Number::class.java).toLong()
        val periodMs = SpelEval.eval(pjp, beanFactory, rl.refillPeriodMs, Number::class.java).toLong()
        val requested = SpelEval.eval(pjp, beanFactory, rl.requested, Number::class.java).toLong()
        val policy = RateLimitPolicy(
            capacity = cap,
            refillTokens = refill,
            refillPeriod = Duration.ofMillis(periodMs),
            requested = requested
        )

        // 执行限流检查（默认消费 1 个令牌）
        val res = limiter.tryConsume(key, policy)
        if (!res.allowed) {
            metrics.onRateLimitBlocked(key.namespace, key.value)
            throw RateLimitExceededException(key)
        }

        return try {
            // 继续执行原方法
            pjp.proceed()
        } catch (ex: Throwable) {
            // 如果方法执行失败且满足退款条件，则尝试退还令牌
            if (rl.refundOn.any { it.java.isAssignableFrom(ex.javaClass) }) {
                limiter.refund(key, requested, policy)
            }
            throw ex
        }
    }
}
