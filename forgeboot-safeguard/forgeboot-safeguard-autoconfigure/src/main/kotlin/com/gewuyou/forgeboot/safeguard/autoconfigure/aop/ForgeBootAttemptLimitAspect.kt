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
import com.gewuyou.forgeboot.safeguard.autoconfigure.resolver.AttemptLimitExceptionFactoryResolver
import com.gewuyou.forgeboot.safeguard.core.api.AttemptLimitManager
import com.gewuyou.forgeboot.safeguard.core.enums.KeyProcessingMode
import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.metrics.SafeguardMetrics
import com.gewuyou.forgeboot.safeguard.core.model.AttemptCheck
import com.gewuyou.forgeboot.safeguard.core.model.AttemptLimitContext
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.Order
import java.time.Duration
import java.time.Instant

/**
 * 尝试限制切面，用于拦截带有 [AttemptLimit] 注解的方法，实现请求频率控制和失败尝试限制。
 *
 * @property attemptManager 尝试次数管理器，用于执行检查、成功或失败的处理逻辑。
 * @property metrics 安全防护指标收集器，用于记录尝试、阻塞、锁定等事件。
 * @property keySupport 密钥解析支持类，用于生成基于方法调用上下文的唯一标识键。
 * @property request 当前 HTTP 请求对象，用于获取客户端 IP 地址。
 * @property resolver 异常工厂解析器，用于解析并生成尝试限制异常。
 *
 * @since 2025-09-22 11:56:09
 * @author gewuyou
 */
@Aspect
@Order(10)
class ForgeBootAttemptLimitAspect(
    private val attemptManager: AttemptLimitManager,
    private val metrics: SafeguardMetrics,
    private val keySupport: KeyResolutionSupport,
    private val request: HttpServletRequest,
    private val resolver: AttemptLimitExceptionFactoryResolver,
) {
    private companion object {
        /**
         * 默认命名空间前缀，用于构建 key 的命名空间。
         */
        const val NS = "safeguard:al"
    }

    /**
     * 获取客户端真实 IP 地址。
     *
     * @param req HTTP 请求对象
     * @return 客户端 IP 地址字符串
     */
    private fun clientIp(req: HttpServletRequest): String =
        (req.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim())
            ?: (req.getHeader("X-Real-IP") ?: req.remoteAddr ?: "0.0.0.0")

    /**
     * 环绕通知方法，处理带 [AttemptLimit] 注解的方法调用。
     *
     * @param pjp 连接点对象，表示被拦截的方法调用
     * @param al 方法上的 [AttemptLimit] 注解实例
     * @return 被拦截方法的返回值
     * @throws RuntimeException 如果尝试次数超出限制，则抛出自定义异常
     */
    @Around("@annotation(al)")
    fun around(pjp: ProceedingJoinPoint, al: AttemptLimit): Any {
        // 构造策略对象
        val policy = al.toPolicy()
        // 获取客户端 IP
        val ip = clientIp(request)
        // 根据模式解析 key
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

        // 执行前置检查
        val pre = attemptManager.onCheck(key, policy)
        val context = buildContext(key, policy, pjp, al, pre)

        // 如果不允许访问，直接抛出异常
        if (!pre.allowed) {
            metrics.onAttemptBlocked(key.namespace, key.full(), pre.lockTtlMs)
            throw constructException(al, context)
        }

        // 先执行业务逻辑
        return try {
            val result = pjp.proceed()

            // 成功后重置尝试计数（根据策略）
            if (policy.successReset) {
                attemptManager.onSuccess(key, policy)
                metrics.onAttemptReset(key.namespace, key.full())
            }
            result
        } catch (ex: Throwable) {
            // 处理失败情况
            val chk = attemptManager.onFail(key, policy)
            val failContext = buildContext(key, policy, pjp, al, chk)

            if (!chk.allowed) {
                // 已锁定，记录指标并抛出异常
                metrics.onAttemptLocked(key.namespace, key.full(), chk.lockTtlMs)
                val e = constructException(al, failContext)
                e.addSuppressed(ex)
                throw e
            } else {
                // 未锁定但已记录失败，更新指标并重新抛出原始异常
                metrics.onAttemptRecorded(key.namespace, key.full(), chk.attemptsTtlMs)
                throw ex
            }
        }
    }

    /**
     * 构建尝试限制上下文对象。
     *
     * @param key 当前操作的 key 对象
     * @param policy 当前尝试限制策略
     * @param pjp 连接点对象
     * @param al 方法上的 [AttemptLimit] 注解实例
     * @param preResult 前置检查结果
     * @return 构造好的 [AttemptLimitContext] 上下文对象
     */
    private fun buildContext(
        key: Key,
        policy: AttemptPolicy,
        pjp: ProceedingJoinPoint,
        al: AttemptLimit,
        preResult: AttemptCheck,
    ): AttemptLimitContext {
        val signature = pjp.signature as MethodSignature
        val method = signature.method
        return AttemptLimitContext(
            key = key,
            remainingMillis = if (preResult.allowed) null else preResult.retryAfterMs,
            scene = al.scene,
            infoCode = al.infoCode,
            joinPoint = pjp,
            method = method,
            now = Instant.now(),
            args = pjp.args,
            annotations = method.annotations,
            remainingAttempts = preResult.remainingAttempts,
            policy = policy,
            currentAnnotation = al,
            retryAt = if (preResult.allowed) null else Instant.now().plusMillis(preResult.retryAfterMs)
        )
    }

    // ---------------- helpers ----------------

    /**
     * 将 [AttemptLimit] 注解转换为 [AttemptPolicy] 策略对象。
     *
     * @receiver al [AttemptLimit] 注解实例
     * @return 构造好的 [AttemptPolicy] 策略对象
     */
    private fun AttemptLimit.toPolicy(): AttemptPolicy =
        AttemptPolicy(
            window = Duration.parse(window),
            max = max,
            lock = Duration.parse(lock),
            escalate = parseEscalate(escalate),
            successReset = successReset
        )

    /**
     * 解析升级规则字符串为 Map<Long, Duration>。
     *
     * @param s 升级规则字符串，格式如 "1:PT10S,3:PT30S"
     * @return 解析后的 Map，key 为触发次数，value 为对应的锁定时长
     */
    private fun parseEscalate(s: String): Map<Long, Duration> =
        if (s.isBlank()) emptyMap() else s.split(",").associate { pair ->
            val (th, iso) = pair.split(":")
            th.trim().toLong() to Duration.parse(iso.trim())
        }

    /**
     * 构造自定义异常对象。
     *
     * @param cd [AttemptLimit] 注解实例
     * @param context 当前尝试限制上下文
     * @return 构造好的异常对象
     */
    private fun constructException(
        cd: AttemptLimit,
        context: AttemptLimitContext,
    ): RuntimeException {
        val factory = resolver.resolve(cd)
        return factory.create(context)
    }
}
