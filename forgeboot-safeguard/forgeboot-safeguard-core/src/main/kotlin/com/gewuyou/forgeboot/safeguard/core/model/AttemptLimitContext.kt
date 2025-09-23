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

package com.gewuyou.forgeboot.safeguard.core.model

import com.gewuyou.forgeboot.safeguard.core.key.Key
import com.gewuyou.forgeboot.safeguard.core.policy.AttemptPolicy
import org.aspectj.lang.ProceedingJoinPoint
import java.lang.reflect.Method
import java.time.Instant

/**
 * 尝试限制上下文
 *
 * 该类用于封装尝试限制（如重试、限流等）相关的上下文信息，继承自 [GuardContext]。
 * 包含当前尝试次数、策略、重置时间等与尝试限制逻辑相关的信息。
 *
 * @property key 用于标识当前操作的键值，通常用于区分不同的限制策略
 * @property remainingMillis 剩余的毫秒数，表示在允许继续操作前需要等待的时间；当为 null 时表示允许操作
 * @property scene 当前操作的场景描述，可用于日志或监控
 * @property infoCode 信息码，用于标识当前状态或错误类型
 * @property joinPoint 切点对象，表示被拦截的方法执行点
 * @property method 被拦截的目标方法
 * @property now 当前时间戳，用于计算时间相关逻辑
 * @property args 方法参数列表
 * @property annotations 目标方法上的注解数组
 * @property remainingAttempts 当前还可失败的次数，即剩余尝试额度
 * @property policy 尝试限制策略，定义最大尝试次数等规则
 * @property retryAt 下次可重试的绝对时间，若当前允许操作则为 null
 *
 * @since 2025-09-23 11:00:14
 * @author gewuyou
 */
data class AttemptLimitContext(
    // ---- GuardContext 公共部分 ----
    override val key: Key,
    override val remainingMillis: Long?,             // = retryAfterMs，相对时间；allowed 时可为 null
    override val scene: String?,
    override val infoCode: String?,
    override val joinPoint: ProceedingJoinPoint,
    override val method: Method,
    override val now: Instant,
    override val args: Array<Any?>,
    override val annotations: Array<Annotation>,

    // ---- AttemptLimit 特有部分 ----
    val remainingAttempts: Long,                     // 当前可失败的“剩余额度”
    val policy: AttemptPolicy,
    val retryAt: Instant?,                           // 绝对时间，可选（allowed 时为 null）
) : GuardContext(
    key,
    remainingMillis,
    scene,
    infoCode,
    joinPoint,
    method,
    now,
    args,
    annotations
) {
    /**
     * 容量：表示最大允许尝试次数，由策略中的 max 字段决定
     */
    val capacity: Long get() = policy.max

    /**
     * 当前已尝试次数：通过容量减去剩余尝试次数得到
     */
    val currentAttempts: Long get() = capacity - remainingAttempts

    /**
     * 向上取整的重试秒数：用于异常提示或文案展示，最小为 1 秒
     */
    val retryAfterSecondsCeil: Long?
        get() = remainingMillis?.let { ms -> ((ms + 999) / 1000).coerceAtLeast(1) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttemptLimitContext) return false

        if (remainingMillis != other.remainingMillis) return false
        if (remainingAttempts != other.remainingAttempts) return false
        if (key != other.key) return false
        if (scene != other.scene) return false
        if (infoCode != other.infoCode) return false
        if (joinPoint != other.joinPoint) return false
        if (method != other.method) return false
        if (now != other.now) return false
        if (!args.contentEquals(other.args)) return false
        if (!annotations.contentEquals(other.annotations)) return false
        if (policy != other.policy) return false
        if (retryAt != other.retryAt) return false
        if (capacity != other.capacity) return false
        if (currentAttempts != other.currentAttempts) return false
        if (retryAfterSecondsCeil != other.retryAfterSecondsCeil) return false

        return true
    }

    override fun hashCode(): Int {
        var result = remainingMillis?.hashCode() ?: 0
        result = 31 * result + remainingAttempts.hashCode()
        result = 31 * result + key.hashCode()
        result = 31 * result + (scene?.hashCode() ?: 0)
        result = 31 * result + (infoCode?.hashCode() ?: 0)
        result = 31 * result + joinPoint.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + now.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + annotations.contentHashCode()
        result = 31 * result + policy.hashCode()
        result = 31 * result + (retryAt?.hashCode() ?: 0)
        result = 31 * result + capacity.hashCode()
        result = 31 * result + currentAttempts.hashCode()
        result = 31 * result + (retryAfterSecondsCeil?.hashCode() ?: 0)
        return result
    }
}
