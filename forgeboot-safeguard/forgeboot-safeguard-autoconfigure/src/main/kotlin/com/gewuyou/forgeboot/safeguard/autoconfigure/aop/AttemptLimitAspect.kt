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

import com.gewuyou.forgeboot.safeguard.autoconfigure.annotations.AttemptLimit
import com.gewuyou.forgeboot.safeguard.autoconfigure.key.KeyResolutionSupport
import com.gewuyou.forgeboot.safeguard.core.api.AttemptLimitManager
import com.gewuyou.forgeboot.safeguard.core.enums.KeyProcessingMode
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.AttemptLimitContext
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import java.time.Duration
import java.time.Instant

/**
 *尝试限制切面
 *
 * @since 2025-09-22 11:56:09
 * @author gewuyou
 */
@Aspect
@Order(10)
class AttemptLimitAspect(
    private val attemptManager: AttemptLimitManager,
    private val metrics: SafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
    private val request: HttpServletRequest,
    private val applicationContext: ApplicationContext,
) {
    private companion object {
        const val NS = "safeguard:al"
    }

    private fun clientIp(req: HttpServletRequest): String =
        (req.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim())
            ?: (req.getHeader("X-Real-IP") ?: req.remoteAddr ?: "0.0.0.0")

    @Around("@annotation(al)")
    fun around(pjp: ProceedingJoinPoint, al: AttemptLimit): Any {
        val policy = al.toPolicy()
        val ip = clientIp(request)
        val key: Key = when (al.mode) {
            KeyProcessingMode.IP_KEY -> {
                keySupport.resolveForPjp(
                    pjp, al.resolverBean, al.template, al.key, defaultNamespace = "$NS:$ip",
                )
            }

            KeyProcessingMode.NONE -> {
                keySupport.resolveForPjp(
                    pjp, al.resolverBean, al.template, al.key, defaultNamespace = "$NS:none",
                )
            }
        }
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        val pre = attemptManager.onCheck(key, policy)
        val context = AttemptLimitContext(
            key,
            pre.lockTtlMs,
            al.scene,
            al.infoCode,
            pjp,
            method,
            Instant.now(),
            pjp.args,
            method.annotations,
            policy.max - pre.remainingAttempts,
            policy.max,
            policy,
            retryAt = if (pre.allowed) null else Instant.now().plusMillis(pre.retryAfterMs),
        )

        if (!pre.allowed) {
            metrics.onAttemptBlocked(key.namespace, key.full(), pre.lockTtlMs)
            throw constructException(al, context)
        }
        // 先执行业务：未抛异常→成功；抛异常→失败
        return try {
            val result = pjp.proceed()

            if (policy.successReset) {
                // 成功：清空窗口/锁（不动 strikes，符合长期惩罚的策略）
                attemptManager.onSuccess(key, policy)
                metrics.onAttemptReset(key.namespace, key.full())
            }
            result
        } catch (ex: Throwable) {
            val chk = attemptManager.onFail(key, policy)
            val failContext = AttemptLimitContext(
                key,
                chk.lockTtlMs,
                al.scene,
                al.infoCode,
                pjp,
                method,
                Instant.now(),
                pjp.args,
                method.annotations,
                policy.max - chk.remainingAttempts,
                policy.max,
                policy,
                retryAt = if (chk.allowed) null else Instant.now().plusMillis(chk.retryAfterMs),
            )
            if (!chk.allowed) {
                // 指标：进入/处于锁定
                metrics.onAttemptLocked(key.namespace, key.full(), chk.lockTtlMs)
                // 建议抛“统一的”业务异常信息；原异常挂到 suppressed 方便排障
                val e = constructException(al, failContext)
                e.addSuppressed(ex)
                throw e
            } else {
                // 指标：失败已计入窗口但未锁定
                metrics.onAttemptRecorded(key.namespace, key.full(), chk.attemptsTtlMs)
                throw ex
            }
        }
    }

    // ---------------- helpers ----------------

    private fun AttemptLimit.toPolicy(): AttemptPolicy =
        AttemptPolicy(
            window = Duration.parse(window),
            max = max,
            lock = Duration.parse(lock),
            escalate = parseEscalate(escalate),
            successReset = successReset
        )

    private fun parseEscalate(s: String): Map<Long, Duration> =
        if (s.isBlank()) emptyMap() else s.split(",").associate { pair ->
            val (th, iso) = pair.split(":")
            th.trim().toLong() to Duration.parse(iso.trim())
        }

    private fun constructException(
        cd: AttemptLimit,
        context: AttemptLimitContext,
    ): RuntimeException {
        val factoryType = cd.factory.java
        val factory = applicationContext.getBeanProvider(factoryType).ifAvailable
            ?: factoryType.getDeclaredConstructor().newInstance()
        return factory.create(context)
    }
}