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

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.Idempotent
import com.gewuyou.forgeboot.safeguard.autoconfigure.key.KeyResolutionSupport
import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.api.IdempotencyManager
import com.gewuyou.forgeboot.safeguard.core.enums.IdemMode
import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus
import com.gewuyou.forgeboot.safeguard.core.exception.IdempotencyConflictException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.IdempotencyRecord
import com.gewuyou.forgeboot.safeguard.core.policy.IdempotencyPolicy
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec
import com.gewuyou.forgeboot.safeguard.redis.config.SafeguardProperties
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.annotation.Order
import java.time.Duration

/**

 * 幂等切面类，用于处理带有 @Idempotent 注解的方法调用，确保方法在一定时间内只执行一次。
 *
 * @property idem 幂等性管理器，用于操作幂等记录。
 * @property codec 请求/响应负载编解码器。
 * @property props 配置属性类，包含幂等相关的配置信息。
 * @property beanFactory Spring Bean 工厂，用于解析 SpEL 表达式。
 * @since 2025-09-21 14:28:05
 * @author gewuyou
 */
@Aspect
@Order(0)
class IdempotentAspect(
    private val idem: IdempotencyManager,
    private val codec: PayloadCodec,
    private val props: SafeguardProperties,
    private val beanFactory: BeanFactory,
    private val metrics: SafeguardMetrics = NoopSafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
) {
    private companion object {
        const val NS = "safeguard:idem"
    }

    /**
     * 切面环绕通知方法，处理标注了 @Idempotent 注解的方法调用。
     *
     * @param pjp 连接点对象，表示被拦截的方法。
     * @param idemAnn 幂等注解对象，包含幂等配置信息。
     * @return 方法执行结果或从幂等记录中返回的结果。
     */
    @Around("@annotation(idemAnn)")
    fun around(pjp: ProceedingJoinPoint, idemAnn: Idempotent): Any? {
        val ttlSec = SpelEval.eval(pjp, beanFactory, idemAnn.ttlSeconds, Number::class.java).toLong()
        val key: Key = keySupport.resolveForPjp(
            pjp, idemAnn.resolverBean, idemAnn.template, idemAnn.key, defaultNamespace = NS
        )
        val policy = IdempotencyPolicy(Duration.ofSeconds(ttlSec), idemAnn.mode)

        // 1) 已有记录的处理（命中/冲突/等待）
        handleExistingRecord(key, idemAnn)
        val acquired = idem.tryAcquirePending(key, policy)
        /// 2) 尝试占位：失败视为并发冲突
        if (!acquired) {
            metrics.onRateLimitBlocked(key.namespace, key.value)
            return around(pjp, idemAnn) // 并发抢占失败时重试
        }
        // 3) 首次 MISS：记录埋点并执行业务
        metrics.onIdemMiss(key.namespace, key.value)
        // 执行业务逻辑并保存结果
        return executeAndSaveResult(pjp, key, policy)
    }

    /**
     * 处理已存在的幂等记录。
     *
     * @param key 幂等键。
     * @param idemAnn 幂等注解对象。
     */
    private fun handleExistingRecord(key: Key, idemAnn: Idempotent) {
        idem[key]?.let { rec ->
            when (rec.status) {
                IdempotencyStatus.SUCCESS -> throw ReturnValueFromRecordException(rec)
                IdempotencyStatus.PENDING -> when (idemAnn.mode) {
                    IdemMode.RETURN_SAVED,
                    IdemMode.CONFLICT_409,
                        -> {
                        metrics.onIdemConflict(key.namespace, key.value)
                        throw IdempotencyConflictException(key)
                    }

                    IdemMode.WAIT_UNTIL_DONE -> waitForCompletion(key)
                }
            }
        }
    }

    /**
     * 等待指定幂等键的执行完成。
     *
     * @param key 幂等键。
     */
    private fun waitForCompletion(key: Key) {
        val deadline = System.currentTimeMillis() + props.idempotencyWaitMax.toMillis()
        while (System.currentTimeMillis() < deadline) {
            Thread.sleep(50)
            val r = idem[key] ?: continue
            if (r.status == IdempotencyStatus.SUCCESS) {
                throw ReturnValueFromRecordException(r)
            }
        }
        throw IdempotencyConflictException(key)
    }

    /**
     * 执行目标方法并将结果保存到幂等记录中。
     *
     * @param pjp 连接点对象。
     * @param key 幂等键。
     * @param policy 幂等策略。
     * @return 方法执行结果。
     */
    private fun executeAndSaveResult(pjp: ProceedingJoinPoint, key: Key, policy: IdempotencyPolicy): Any? {
        return try {
            val out = pjp.proceed()
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

    /**
     * 自定义异常类，用于在检测到已有成功记录时提前返回结果。
     *
     * @property record 已存在的幂等记录。
     */
    private class ReturnValueFromRecordException(val record: IdempotencyRecord) : RuntimeException()
}
