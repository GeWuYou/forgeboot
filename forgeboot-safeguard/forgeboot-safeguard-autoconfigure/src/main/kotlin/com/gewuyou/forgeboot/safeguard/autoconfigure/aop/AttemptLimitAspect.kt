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
import com.gewuyou.forgeboot.safeguard.core.exception.AttemptLimitExceededException
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import java.time.Duration

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
        val pre = attemptManager.onCheck(key, policy)
        if (!pre.allowed) {
            metrics.onAttemptBlocked(key.namespace, key.full(), pre.lockTtlMs)
            throw AttemptLimitExceededException(key)
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
            if (!chk.allowed) {
                // 指标：进入/处于锁定
                metrics.onAttemptLocked(key.namespace, key.full(), chk.lockTtlMs)
                // 建议抛“统一的”业务异常信息；原异常挂到 suppressed 方便排障
                val e = AttemptLimitExceededException(key)
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

    private fun parseEscalate(s: String): Map<Int, Duration> =
        if (s.isBlank()) emptyMap() else s.split(",").associate { pair ->
            val (th, iso) = pair.split(":")
            th.trim().toInt() to Duration.parse(iso.trim())
        }
}