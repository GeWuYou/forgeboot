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
 * @property key 上下文的唯一标识键
 * @property retryAfterMillis 剩余时间（毫秒），可为空
 * @property scene 场景标识，可为空
 * @property infoCode 信息码，可为空
 * @property joinPoint 切点对象，表示被拦截的方法执行点
 * @property method 被拦截的方法对象
 * @property now 当前时间戳
 * @property args 方法参数数组
 * @property annotations 方法上的注解数组
 * @property currentAttempts 当前已尝试次数，可为空
 * @property max 最大尝试次数
 * @property policy 尝试限制策略
 * @property retryAt 下次重置时间，可为空
 *
 * @since 2025-09-23 11:00:14
 * @author gewuyou
 */
data class AttemptLimitContext(
    override val key: Key,
    val retryAfterMillis: Long?,
    override val scene: String?,
    override val infoCode: String?,
    override val joinPoint: ProceedingJoinPoint,
    override val method: Method,
    override val now: Instant,
    override val args: Array<Any?>,
    override val annotations: Array<Annotation>,
    val currentAttempts: Long?,
    val max: Long,
    val policy: AttemptPolicy,
    val retryAt: Instant?,
) : GuardContext(
    key,
    retryAfterMillis,
    scene,
    infoCode,
    joinPoint,
    method,
    now,
    args,
    annotations
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttemptLimitContext) return false

        if (retryAfterMillis != other.retryAfterMillis) return false
        if (currentAttempts != other.currentAttempts) return false
        if (max != other.max) return false
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

        return true
    }

    override fun hashCode(): Int {
        var result = retryAfterMillis?.hashCode() ?: 0
        result = 31 * result + (currentAttempts?.hashCode() ?: 0)
        result = 31 * result + max.hashCode()
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
        return result
    }

}
