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
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.IdempotentExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.autoconfigure.spel.SpelEval
import com.gewuyou.forgeboot.safeguard.core.api.IdempotencyManager
import com.gewuyou.forgeboot.safeguard.core.enums.IdemMode
import com.gewuyou.forgeboot.safeguard.core.enums.IdempotencyStatus
import com.gewuyou.forgeboot.safeguard.core.exception.IdempotentReturnValueFromRecordException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.NoopSafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.IdempotentContext
import com.gewuyou.forgeboot.safeguard.core.model.IdempotentRecord
import com.gewuyou.forgeboot.safeguard.core.policy.IdempotentPolicy
import com.gewuyou.forgeboot.safeguard.core.serialize.PayloadCodec
import com.gewuyou.forgeboot.safeguard.redis.config.SafeguardProperties
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.BeanFactory
import org.springframework.core.annotation.Order
import java.time.Duration
import java.time.Instant

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
class ForgeBootIdempotentAspect(
    private val idem: IdempotencyManager,
    private val codec: PayloadCodec,
    private val props: SafeguardProperties,
    private val beanFactory: BeanFactory,
    private val metrics: SafeguardMetrics = NoopSafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
    private val resolver: IdempotentExceptionFactoryResolver,
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
        val policy = IdempotentPolicy(Duration.ofSeconds(ttlSec), idemAnn.mode)
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val context = IdempotentContext(
            key,
            idemAnn.scene,
            idemAnn.infoCode,
            pjp,
            method,
            Instant.now(),
            pjp.args,
            idemAnn,
            method.annotations
        )
        // 1) 已有记录的处理（命中/冲突/等待）
        handleExistingRecord(key, idemAnn, context)
        val acquired = idem.tryAcquirePending(key, policy)
        /// 2) 尝试占位：失败视为并发冲突
        if (!acquired) {
            Thread.sleep(20)
            idem[key]?.let { rec ->
                if (rec.status == IdempotencyStatus.SUCCESS) throw IdempotentReturnValueFromRecordException(key, rec)
            }
            metrics.onIdemConflict(key.namespace, key.value)
            throw constructException(idemAnn, context)
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
     * @param context 幂等上下文对象。
     */
    private fun handleExistingRecord(key: Key, idemAnn: Idempotent, context: IdempotentContext) {
        idem[key]?.let { rec ->
            // 根据记录状态进行不同处理
            when (rec.status) {
                IdempotencyStatus.SUCCESS -> throw IdempotentReturnValueFromRecordException(key, rec)
                IdempotencyStatus.PENDING -> when (idemAnn.mode) {
                    IdemMode.RETURN_SAVED,
                    IdemMode.THROW_EXCEPTION,
                        -> {
                        metrics.onIdemConflict(key.namespace, key.value)
                        throw constructException(idemAnn, context)
                    }

                    IdemMode.WAIT_UNTIL_DONE -> waitForCompletion(key, idemAnn, context)
                }
            }
        }
    }


    /**
     * 构造运行时异常实例
     *
     * @param idempotent 幂等注解对象，用于获取异常工厂类型
     * @param context 幂等性上下文，包含异常构造所需的信息
     * @return 构造好的运行时异常实例
     */
    private fun constructException(
        idempotent: Idempotent,
        context: IdempotentContext,
    ): RuntimeException {
        // 获取异常工厂
        val factory = resolver.resolve(idempotent)
        // 使用工厂创建并返回运行时异常
        return factory.create(context)
    }

    /**
     * 等待指定幂等键的执行完成。
     *
     * @param key 幂等键，用于标识唯一的幂等操作。
     * @param idemAnn 幂等注解，包含幂等配置信息。
     * @param context 幂等上下文，提供执行环境相关信息。
     */
    private fun waitForCompletion(key: Key, idemAnn: Idempotent, context: IdempotentContext) {
        // 计算等待超时时间
        val deadline = System.currentTimeMillis() + props.idempotencyWaitMax.toMillis()
        // 轮询检查执行状态直到超时
        while (System.currentTimeMillis() < deadline) {
            Thread.sleep(50)
            val r = idem[key] ?: continue
            // 如果执行成功，则抛出返回值异常
            if (r.status == IdempotencyStatus.SUCCESS) {
                throw IdempotentReturnValueFromRecordException(key, r)
            }
        }
        // 等待超时后抛出异常
        throw constructException(idemAnn, context)
    }


    /**
     * 执行目标方法并将结果保存到幂等记录中。
     *
     * @param pjp 连接点对象。
     * @param key 幂等键。
     * @param policy 幂等策略。
     * @return 方法执行结果。
     */
    private fun executeAndSaveResult(pjp: ProceedingJoinPoint, key: Key, policy: IdempotentPolicy): Any? {
        return try {
            val out = pjp.proceed()
            idem.saveSuccess(
                key,
                IdempotentRecord(
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
