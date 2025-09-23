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
import org.aspectj.lang.ProceedingJoinPoint
import java.lang.reflect.Method
import java.time.Instant

/**
 * 冷却上下文
 *
 * 用于封装与冷却逻辑相关的上下文信息，包括方法调用的元数据、注解信息以及冷却状态等。
 *
 * @property key 唯一标识当前冷却操作的键值，通常用于区分不同的冷却实例。
 * @property remainingMillis 剩余的冷却时间（毫秒），如果无法获取 TTL 则为 null。
 * @property scene 场景标识，通常由注解传入，用于指定冷却策略的应用场景。
 * @property infoCode 信息码，通常由注解传入，用于附加描述或分类冷却行为。
 * @property joinPoint Spring AOP 的连接点对象，表示被拦截的方法执行点。
 * @property method 被拦截的目标方法对象。
 * @property now 当前时间点，通常由系统提供。
 * @property args 方法参数数组。
 * @property annotations 目标方法上携带的所有注解。
 *
 * @since 2025-09-23 10:25:07
 * @author gewuyou
 */
data class CooldownContext(
    override val key: Key,
    override val remainingMillis: Long?,
    override val scene: String?,
    override val infoCode: String?,
    override val joinPoint: ProceedingJoinPoint,
    override val method: Method,
    override val now: Instant,
    override val args: Array<Any?>,
    override val annotations: Array<Annotation>,
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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CooldownContext) return false

        if (remainingMillis != other.remainingMillis) return false
        if (key != other.key) return false
        if (scene != other.scene) return false
        if (infoCode != other.infoCode) return false
        if (joinPoint != other.joinPoint) return false
        if (method != other.method) return false
        if (now != other.now) return false
        if (!args.contentEquals(other.args)) return false
        if (!annotations.contentEquals(other.annotations)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = remainingMillis?.hashCode() ?: 0
        result = 31 * result + key.hashCode()
        result = 31 * result + (scene?.hashCode() ?: 0)
        result = 31 * result + (infoCode?.hashCode() ?: 0)
        result = 31 * result + joinPoint.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + now.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + annotations.contentHashCode()
        return result
    }

}
